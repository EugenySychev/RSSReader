package com.sychev.rss_reader.rss_reader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.UrlQuerySanitizer;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

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

public class NewsDataLoader extends Thread {
    int timeout;
    URL source;
    public List<NewsModelItem> loadedList;

    Handler handler;

    NewsDataLoader(URL source, int timeout) {
        this.source = source;
        this.timeout = timeout;
        loadedList = new ArrayList<>();
    }

    public void run() {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(source.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");
            System.out.printf("Readed %d elements", nodeList.getLength());

            ArrayList<NewsModelItem> list = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                Element fstElmnt = (Element) node;
                NodeList nameList = fstElmnt.getElementsByTagName("title");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();

                NodeList descrList = fstElmnt.getElementsByTagName("description");
                Element descrListElement = (Element) descrList.item(0);
                descrList = descrListElement.getChildNodes();
                System.out.printf("Website = %s", ((Node) descrList.item(0)).getNodeValue());
                NewsModelItem item = new NewsModelItem(((Node) nameList.item(0)).getNodeValue(), ((Node) descrList.item(0)).getNodeValue());

                NodeList iconList = fstElmnt.getElementsByTagName("enclosure");
                String urlStr = iconList.item(0).getAttributes().getNamedItem("url").getNodeValue();
                URL urlBitmap = new URL(urlStr);
                Bitmap loadedBitmap = BitmapFactory.decodeStream(urlBitmap.openConnection().getInputStream());

                item.setIcon(loadedBitmap);
                loadedList.add(item);
            }

            Message msg = handler.obtainMessage();
            msg.obj = this;
            handler.sendMessage(msg);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }


    public void setHandler(Handler mHandler) {
        handler = mHandler;
    }
}
