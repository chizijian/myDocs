package com.lovelyhq.lovelydocs.helpers;

import com.google.android.gms.plus.PlusShare;
import com.lovelyhq.lovelydocs.services.DownloadService;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request.Builder;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlParser {
    public static final String XML_URL = "https://raw.githubusercontent.com/Kapeli/feeds/master/";

    private static String getXmlFromUrl(String url) throws IOException {
        return new OkHttpClient().newCall(new Builder().url(url).build()).execute().body().string();
    }

    public static List<String> parseVersions(String docsetUrl) {
        String url = XML_URL + docsetUrl + ".xml";
        List<String> versions = new ArrayList();
        try {
            String xml = getXmlFromUrl(url);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xml));
            for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
                if (eventType == 2 && xpp.getName().equals(DownloadService.ARG_VERSION)) {
                    eventType = xpp.next();
                    if (eventType == 2 && xpp.getName().equals("name")) {
                        eventType = xpp.next();
                        versions.add(xpp.getText());
                    } else if (eventType == 4) {
                        versions.add("Latest (" + xpp.getText().replace("/", "_") + ")");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e2) {
            e2.printStackTrace();
        }
        return versions;
    }

    public static List<String> parseServers(String docsetUrl) {
        String url = XML_URL + docsetUrl + ".xml";
        List<String> servers = new ArrayList();
        try {
            String xml = getXmlFromUrl(url);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xml));
            for (int eventType = xpp.getEventType(); eventType != 1; eventType = xpp.next()) {
                if (eventType == 2 && xpp.getName().equals(PlusShare.KEY_CALL_TO_ACTION_URL)) {
                    eventType = xpp.next();
                    servers.add(xpp.getText());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e2) {
            e2.printStackTrace();
        }
        return servers;
    }
}
