package com.sychev.rss_reader.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;

import com.sychev.rss_reader.Utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class NewsNetworkLoader extends Thread {
    private final List<NewsModelItem> loadedList;
    private final SourceModelItem sourceItem;
    private URL source;
    private Handler handler;
    private boolean needUpdateSource = false;

    public NewsNetworkLoader(SourceModelItem source) {
        sourceItem = source;
        loadedList = new ArrayList<>();
    }

    @Override
    public void run() {
        Message startLoadMsg = handler.obtainMessage();
        startLoadMsg.obj = this;
        startLoadMsg.what = LoadState.LOAD_PROCESSING;
        handler.sendMessage(startLoadMsg);

        try {
            needUpdateSource = false;
            source = new URL(sourceItem.getUrl());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(source.openStream()));
            doc.getDocumentElement().normalize();

            Element elem = (Element) doc.getElementsByTagName("channel").item(0);
            String title = getValueFromElement(elem, "title");
            if (!title.equals(sourceItem.getTitle())) {
                sourceItem.setTitle(title);
                needUpdateSource = true;
            }

            if (sourceItem.getIcon() == null) {
                URL iconSource = new URL(source.getProtocol() + "://" + source.getHost() + "/favicon.ico");
                Bitmap icon = BitmapFactory.decodeStream(iconSource.openConnection().getInputStream());
                if (icon != null) {
                    sourceItem.setIcon(icon);
                    sourceItem.setIconUrl(iconSource.toString());
                    ImageCache.getInstance().saveBitmapToCahche(iconSource.toString(), icon);

                }
            }
            NodeList nodeList = doc.getElementsByTagName("item");
            System.out.printf("Readed %d elements", nodeList.getLength());

            ArrayList<NewsModelItem> list = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                NewsModelItem item = getItemFromXmlNode(nodeList.item(i));
                loadedList.add(item);
            }
            Message completeLoadMsg = handler.obtainMessage();
            completeLoadMsg.obj = this;
            completeLoadMsg.what = LoadState.LOAD_OK;
            handler.sendMessage(completeLoadMsg);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            Message errorLoadMsg = handler.obtainMessage();
            errorLoadMsg.what = LoadState.LOAD_ERROR;
            handler.sendMessage(errorLoadMsg);
        }
    }

    private NewsModelItem getItemFromXmlNode(Node node) throws IOException {

        Element fstElmnt = (Element) node;

        String titleText = getValueFromElement(fstElmnt, "title");
        String timeString = getValueFromElement(fstElmnt, "pubDate");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, d LLL yyyy HH:mm:ss Z", Locale.ENGLISH); //Mon, 14 Sep 2020 17:10:06 +0300
        LocalDateTime d = LocalDateTime.parse(timeString, formatter);
        long timeMils = d.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

//        String descrText = getValueFromElement(fstElmnt, "description");
        String descrText = "";
        NodeList descrList = fstElmnt.getElementsByTagName("description");
        Element descrListElement = (Element) descrList.item(0);
        NodeList descrListNodes = descrListElement.getChildNodes();

        for (int i = 0; i < descrListNodes.getLength(); i++) {
            descrText += descrListNodes.item(i).getNodeValue();

        }

        String urlString = "";

        if (fstElmnt.getElementsByTagName("link").item(0) != null)
            urlString = getValueFromElement(fstElmnt, "link");
        else if (fstElmnt.getElementsByTagName("guid").item(0) != null)
            urlString = getValueFromElement(fstElmnt, "guid");

        final NewsModelItem item = new NewsModelItem(titleText, descrText);

        Bitmap loadedBitmap = null;
        NodeList iconList = fstElmnt.getElementsByTagName("enclosure");
        String urlStr = "";
        if (iconList.item(0) != null)
            urlStr = iconList.item(0).getAttributes().getNamedItem("url").getNodeValue();

        if (!urlStr.isEmpty()) {
            item.setIconUrl(urlStr);
            if (ImageCache.getInstance().retrieveBitmapFromCache(urlStr) == null) {
                URL urlBitmap = new URL(urlStr);
                System.out.println("Storing " + urlStr + " as image");
                loadedBitmap = BitmapFactory.decodeStream(urlBitmap.openConnection().getInputStream());
                ImageCache.getInstance().saveBitmapToCahche(urlStr, loadedBitmap);
            } else {
                loadedBitmap = ImageCache.getInstance().retrieveBitmapFromCache(urlStr);
                System.out.println("Loaded image " + loadedBitmap.getByteCount() + " bytes size from cache");
            }
            item.setIcon(loadedBitmap);
        }
        if (timeMils > 0) {
            item.setTime(timeMils);
        }

        final Bitmap finalLoadedBitmap = loadedBitmap;
        Spanned descrTextSpan = Html.fromHtml(descrText,
                Html.FROM_HTML_MODE_LEGACY,
                new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String sourceFromDescr) {
                        URL sourceUrl;

                        try {
                            String sourceFromDescrString = sourceFromDescr;
                            if (sourceFromDescrString.startsWith("//")) {
                                sourceFromDescrString = "https:" + sourceFromDescrString;
                            }
                            sourceUrl = new URL(sourceFromDescrString);

                            Bitmap bitmap = BitmapFactory.decodeStream(sourceUrl.openStream());
                            if (bitmap != null && finalLoadedBitmap == null) {
                                ImageCache.getInstance().saveBitmapToCahche(sourceFromDescrString, bitmap);
                                item.setIcon(bitmap);
                                item.setIconUrl(sourceFromDescrString);
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }, new Html.TagHandler() {
                    @Override
                    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
                    }
                });
        String descrTextRes = Utils.trimString(descrTextSpan.toString());
        char bjChar = 0xfffc;
        char spChar = 0x0;
        descrTextRes = descrTextRes.replace(bjChar, spChar);
        item.setDescription(descrTextRes);
        item.setUrl(urlString);
        item.setSource(source.toString());

        return item;
    }

    private String getValueFromElement(Element element, String name) {
        if (element != null) {
            NodeList nodeList = element.getElementsByTagName(name);
            if (nodeList != null) {
                Element nodeElement = (Element) nodeList.item(0);
                nodeList = nodeElement.getChildNodes();
                return nodeList.item(0).getNodeValue();
            }
        }
        return "";
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public List<NewsModelItem> getLoadedList() {
        return loadedList;
    }

    public boolean isNeedUpdateSource() {
        return needUpdateSource;
    }

    public interface LoadState {
        int LOAD_ERROR = -1;
        int LOAD_OK = 0;
        int LOAD_PROCESSING = 1;
    }
}
