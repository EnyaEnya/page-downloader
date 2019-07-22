import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
        List<String> listOfLinks = getLinksByTag(doc);

        for (String link : listOfLinks) {
            Document newDoc = getPage(link);
            savePage(newDoc);
            System.out.println(link + " saved!");
        }
    }

    private Document getPage(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
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

    public void saveImages(Document document) {


    }

    private List<String> getLinksByTag(Document doc) {

        ArrayList<String> listOfLinks = new ArrayList<String>();

        Elements listNews = doc.getElementsByTag("a");

        for (Element link : listNews) {
            listOfLinks.add(link.attributes().get("href"));
        }

        for (String link : listOfLinks) {
            if (link.startsWith("//")) {
                Collections.replaceAll(listOfLinks, link, uri.getScheme() + ":" + link);
            }
        }

        return listOfLinks;
    }
}
