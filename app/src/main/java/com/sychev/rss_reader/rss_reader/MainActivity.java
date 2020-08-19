package com.sychev.rss_reader.rss_reader;

import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // создаем адаптер
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        loadData();
    }

    protected void loadData() {
        try {
            //set the download URL, a url that points to a file on the internet
            //this is the file to be downloaded
            URL url = new URL("https://www.gazeta.ru/export/rss/first.xml");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");
            System.out.printf("Readed %d elements", nodeList.getLength());

            ArrayList<NewsModelItem> list = new ArrayList<>();
            ListView lvMain = findViewById(R.id.lvMain);


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
                list.add(new NewsModelItem(((Node) nameList.item(0)).getNodeValue(), ((Node) descrList.item(0)).getNodeValue()));


            }
            NewsAdapter adapter = new NewsAdapter(this, list);
            lvMain.setAdapter(adapter);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

}
