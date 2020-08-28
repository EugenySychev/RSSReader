package com.sychev.rss_reader.rss_reader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.UrlQuerySanitizer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class NewsDataLoader extends Thread {
    int timeout;
    URL source;
    public List<NewsModelItem> loadedList;

    Handler handler;
    interface LoadState {
        int LOAD_ERROR = -1;
        int LOAD_OK = 0;
        int LOAD_PROCESSING = 1;
    }

    NewsDataLoader(URL source, int timeout) {
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

    @NonNull
    private NewsModelItem getItemFromXmlNode(Node node) throws IOException {

        Element fstElmnt = (Element) node;
        NodeList nameList = fstElmnt.getElementsByTagName("title");
        Element nameElement = (Element) nameList.item(0);
        nameList = nameElement.getChildNodes();

        NodeList descrList = fstElmnt.getElementsByTagName("description");
        Element descrListElement = (Element) descrList.item(0);
        descrList = descrListElement.getChildNodes();

        NodeList iconList = fstElmnt.getElementsByTagName("enclosure");
        String urlStr = iconList.item(0).getAttributes().getNamedItem("url").getNodeValue();
        URL urlBitmap = new URL(urlStr);
        Bitmap loadedBitmap = BitmapFactory.decodeStream(urlBitmap.openConnection().getInputStream());

        NewsModelItem item = new NewsModelItem(((Node) nameList.item(0)).getNodeValue(), ((Node) descrList.item(0)).getNodeValue());

        item.setIcon(loadedBitmap);
        return item;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}