import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

public class PageDownloader {

    private static final String folderPath = "/var/www/downloadedPage/";
    private URI uri;

    public void download(String url) throws IOException, URISyntaxException {

        uri = new URI(url);

        Document doc = getPage(url);
        savePage(doc);

        savePages(formUrlList(getSourcesByTag(doc, "a", "href")));
        saveImages(formUrlList(getSourcesByTag(doc, "img", "src")));
        saveDownloadableContent(formUrlList(getSourcesByTag(doc, "link", "href")));
        saveDownloadableContent(formUrlList(getSourcesByTag(doc, "script", "src")));

    }

    private Document getPage(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
    }

    private void savePages(HashMap<String, Boolean> listOfLinks) throws IOException {
        for (Map.Entry<String, Boolean> entry : listOfLinks.entrySet()) {
            String k = entry.getKey();
            Boolean v = entry.getValue();
            Document newDoc = null;
            try {
                newDoc = getPage(k);
                v = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            setNewLink(newDoc, "a" ,"href");
            setNewLink(newDoc, "img", "src"); //SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
            setNewLink(newDoc, "link", "href");
            setNewLink(newDoc, "script", "src");
            savePage(newDoc);
        }

    }

    private void savePage(Document document) {
        File file = new File(folderPath + DigestUtils.sha256Hex(document.title()) + ".html");
        try {
            FileUtils.touch(file);
            FileUtils.writeStringToFile(file, document.toString(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getSourcesByTag(Document doc, String tagName, String attribute) {

        ArrayList<String> listOfLinks = new ArrayList<String>();

        Elements listNews = doc.getElementsByTag(tagName);

        for (Element link : listNews) {
            String attr = link.attributes().get(attribute);
            if (!"".equals(attr)) {
                listOfLinks.add(attr);
            }
        }

        for (String link : listOfLinks) {
            if (link.startsWith("//")) {
                Collections.replaceAll(listOfLinks, link, uri.getScheme() + ":" + link);
            }
        }
        return listOfLinks;
    }

    public void saveImages(HashMap<String, Boolean> listOfLinks) throws IOException {
        listOfLinks.forEach((k, v) -> {
            try {
                saveContent(k);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void saveDownloadableContent(HashMap<String, Boolean> listOfLinks) throws IOException {
        listOfLinks.forEach((k, v) -> {
            try {
                saveContent(k);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void saveContent(String contentUrl) throws IOException {

        String imgName = folderPath + contentUrl.substring(contentUrl.lastIndexOf("/"));

        try (BufferedInputStream in = new BufferedInputStream(new URL(contentUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(imgName)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // handle exception
        }
    }

    private void formElement(Document doc) {
        Elements div1 = doc.getElementsByAttribute("href");
    }

    private void setNewLink(Document doc, String tag, String attr) {
        String newUrl = "/var/www/downloadedPage/";
        Elements elements = doc.getElementsByTag(tag);

        for (Element link : elements) {
           link.attributes().put(attr, newUrl);
        }

    }

    private HashMap<String, Boolean> formUrlList(List<String> urlList) {
        HashMap<String, Boolean> listOfUrl = new HashMap<>();
        for (String url : urlList) {
            if (!listOfUrl.containsKey(url)) {
                listOfUrl.put(url, false);
            }
        }
        return listOfUrl;
    }

}
