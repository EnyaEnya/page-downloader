import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.nio.charset.Charset;

public class PageDownloader {

    public void writeToFile(SimpleHttpGet httpGet, HttpGet request) {
        File file = new File("/var/www/downloadedPage.html");
        try {
            FileUtils.touch(file);
            FileUtils.writeStringToFile(file, httpGet.sendHttpGet(request), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
