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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class NewsNetworkLoader extends Thread {
    private List<NewsModelItem> loadedList;
    private int timeout;
    private URL source;
    private Handler handler;

    NewsNetworkLoader(URL source, int timeout) {
        this.source = source;
        this.timeout = timeout;
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
        NodeList nameList = fstElmnt.getElementsByTagName("title");
        Element nameElement = (Element) nameList.item(0);
        nameList = nameElement.getChildNodes();
        String titleText = ((Node) nameList.item(0)).getNodeValue();

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



        System.out.println("Retrieved item title " + titleText + " and description: " + descrText);
        NodeList urlDescr = fstElmnt.getElementsByTagName("guid");
        Element urlDescrElement = (Element) urlDescr.item(0);
        urlDescr = urlDescrElement.getChildNodes();
        String urlString = urlDescr.item(0).getNodeValue();

        Bitmap loadedBitmap;

        if (ImageCache.getInstance().retrieveBitmapFromCache(urlString) == null) {
            NodeList iconList = fstElmnt.getElementsByTagName("enclosure");
            String urlStr = iconList.item(0).getAttributes().getNamedItem("url").getNodeValue();
            URL urlBitmap = new URL(urlStr);
            loadedBitmap = BitmapFactory.decodeStream(urlBitmap.openConnection().getInputStream());
            ImageCache.getInstance().saveBitmapToCahche(urlString, loadedBitmap);
        } else {
            loadedBitmap = ImageCache.getInstance().retrieveBitmapFromCache(urlString);
            System.out.println("Loaded image " + loadedBitmap.getByteCount() + " bytes size from cache");
        }
        NewsModelItem item = new NewsModelItem(titleText, descrText);
        item.setUrl(urlDescr.item(0).getNodeValue());
        item.setIcon(loadedBitmap);
        return item;
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
