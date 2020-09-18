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
import java.sql.Struct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SourceNetworkLoader extends Thread {
    private String title;
    private String url;
    private Handler handler;

    SourceNetworkLoader(String source) {
        url = source;
    }

    @Override
    public void run() {
        Message startLoadMsg = handler.obtainMessage();
        startLoadMsg.obj = this;
        startLoadMsg.what = NewsNetworkLoader.LoadState.LOAD_PROCESSING;
        handler.sendMessage(startLoadMsg);

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            URL sourceUrl = new URL(url);
            Document doc = db.parse(new InputSource(sourceUrl.openStream()));
            doc.getDocumentElement().normalize();

            Element elem = (Element) doc.getElementsByTagName("channel").item(0);

            title = getValueFromElement(elem, "title");


            Message completeLoadMsg = handler.obtainMessage();
            completeLoadMsg.obj = this;
            completeLoadMsg.what = NewsNetworkLoader.LoadState.LOAD_OK;
            handler.sendMessage(completeLoadMsg);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            Message errorLoadMsg = handler.obtainMessage();
            errorLoadMsg.what = NewsNetworkLoader.LoadState.LOAD_ERROR;
            handler.sendMessage(errorLoadMsg);
        }
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

    public String getTitle() {
        return title;
    }

    interface LoadState {
        int LOAD_ERROR = -1;
        int LOAD_OK = 0;
        int LOAD_PROCESSING = 1;
    }
}
