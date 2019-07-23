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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PageDownloader {

    private static final String folderPath = "/var/www/downloadedPage/";
    private URI uri;

    public void download(String url) throws IOException, URISyntaxException {

        uri = new URI(url);

        Document doc = getPage(url);
        savePage(doc);

        savePages(getSourcesByTag(doc, "a", "href"));
        saveImages(getSourcesByTag(doc, "img", "src"));


    }

    private Document getPage(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
    }

    private void savePages(List<String> listOfLinks) throws IOException {
        for (String link : listOfLinks) {
            Document newDoc = getPage(link);
            savePage(newDoc);
            System.out.println(link + " saved!");
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

    public List<String> getSourcesByTag(Document doc, String tagName, String attribute) {

        ArrayList<String> listOfLinks = new ArrayList<String>();

        Elements listNews = doc.getElementsByTag(tagName);

        for (Element link : listNews) {
            listOfLinks.add(link.attributes().get(attribute));
        }

        for (String link : listOfLinks) {
            if (link.startsWith("//")) {
                Collections.replaceAll(listOfLinks, link, uri.getScheme() + ":" + link);
            }
        }
        return listOfLinks;
    }

    public void saveImages(List<String> listOfLinks) throws IOException {
        for (String link : listOfLinks) {
            saveImage(link);
        }
    }

    public static void saveImage(String imageUrl) throws IOException {

        String imgName = folderPath + imageUrl.substring(imageUrl.lastIndexOf("/"));

        try (BufferedInputStream in = new BufferedInputStream(new URL(imageUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(imgName)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // handle exception
        }
    }


}
