import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class PageDownloader {

    private static final String folderPath = "/var/www/downloadedPage/";
    private URI uri;
    private Set<String> urls = new HashSet<>();

    public void download(String url) throws IOException, URISyntaxException {

        uri = new URI(url);

        Document doc = getPage(url);

        saveSourcesByTag(doc, "img", "src");
        saveSourcesByTag(doc, "link", "href");
        saveSourcesByTag(doc, "script", "src");
        savePage(doc);

    }

    private Document getPage(String url) throws IOException {

        return Jsoup.connect(url)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .get();
    }


    private void savePage(Document document) {
        File file = new File(folderPath + URLEncoder.encode(document.baseUri().replace("/", "").replace(":", ""), StandardCharsets.UTF_8) + ".html");
        try {
            FileUtils.touch(file);
            FileUtils.writeStringToFile(file, document.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSourcesByTag(Document doc, String tagName, String attribute) {

        Elements elements = doc.getElementsByTag(tagName);

        for (Element element : elements) {
            String link = element.attributes().get(attribute);
            if ("".equals(link)) {
                continue;
            }
            if ("canonical".equals(element.attributes().get("rel"))) {
                continue;
            }
            if (link.startsWith("//")) {
                link = uri.getScheme() + ":" + link;
            }
            if (link.startsWith("/")) {
                link = uri.getScheme() + "://" + uri.getHost() + link;
            }
            if (urls.contains(link)) {
                continue;
            }
            urls.add(link);
            String newUrl = saveContent(URLEncoder.encode(doc.baseUri().replace("/", "").replace(":", ""), StandardCharsets.UTF_8), link);
            element.attributes().put(attribute, newUrl);
        }

    }


    private String saveContent(String folder, String contentUrl) {
        String contentPath = folder + "/" + contentUrl.substring(contentUrl.lastIndexOf("/"));
        contentPath = contentPath.split("\\?")[0];
        String imgName = folderPath + contentPath;
        File file = new File(imgName);
        try {
            FileUtils.touch(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedInputStream in = new BufferedInputStream(new URL(contentUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "./" + contentPath;
    }

}
