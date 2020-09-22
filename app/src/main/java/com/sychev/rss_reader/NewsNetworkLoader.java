package com.sychev.rss_reader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class NewsNetworkLoader extends Thread {
    private List<NewsModelItem> loadedList;
    private int timeout;
    private URL source;
    private Handler handler;

    NewsNetworkLoader(URL source) {
        this.source = source;
        loadedList = new ArrayList<>();
    }

    @Override
    public void run() {
        Message startLoadMsg = handler.obtainMessage();
        startLoadMsg.obj = this;
        startLoadMsg.what = LoadState.LOAD_PROCESSING;
        handler.sendMessage(startLoadMsg);

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(source.openStream()));
            doc.getDocumentElement().normalize();

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH); //Mon, 14 Sep 2020 17:10:06 +0300
        LocalDateTime d = LocalDateTime.parse(timeString, formatter);
        long timeMils = d.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();

        String descrText = "";
        NodeList descrList = fstElmnt.getElementsByTagName("description");

        int i = 0;
        while (descrText.length() < 11) {
            Element descrListElement = (Element) descrList.item(0);
            NodeList descrListNodes = descrListElement.getChildNodes();
            descrText = ((Node) descrListNodes.item(i)).getNodeValue();
            descrText = descrText.replace("<![CDATA[", "").replace("]]>", "");
            i++;
        }

        String urlString = getValueFromElement(fstElmnt, "guid");
        NewsModelItem item = new NewsModelItem(titleText, descrText);

        Bitmap loadedBitmap;

        if (ImageCache.getInstance().retrieveBitmapFromCache(urlString) == null) {
            NodeList iconList = fstElmnt.getElementsByTagName("enclosure");
            String urlStr = iconList.item(0).getAttributes().getNamedItem("url").getNodeValue();
            URL urlBitmap = new URL(urlStr);
            item.setIconUrl(urlStr);
            loadedBitmap = BitmapFactory.decodeStream(urlBitmap.openConnection().getInputStream());
            ImageCache.getInstance().saveBitmapToCahche(urlString, loadedBitmap);
        } else {
            loadedBitmap = ImageCache.getInstance().retrieveBitmapFromCache(urlString);
            System.out.println("Loaded image " + loadedBitmap.getByteCount() + " bytes size from cache");
        }
        if (timeMils > 0) {
            item.setTime(timeMils);
        }

        item.setUrl(urlString);
        item.setIcon(loadedBitmap);
        item.setSource(source.toString());

        return item;
    }

    private String getValueFromElement(Element element, String name) {
        NodeList nodeList = element.getElementsByTagName(name);
        Element nodeElement = (Element) nodeList.item(0);
        nodeList = nodeElement.getChildNodes();
        return nodeList.item(0).getNodeValue();
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public List<NewsModelItem> getLoadedList() {
        return loadedList;
    }

    interface LoadState {
        int LOAD_ERROR = -1;
        int LOAD_OK = 0;
        int LOAD_PROCESSING = 1;
    }
}
