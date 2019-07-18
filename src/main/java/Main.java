import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        HttpGet request = new HttpGet("https://yandex.ru/");

        PageDownloader pageDownloader = new PageDownloader();

        SimpleHttpGet simpleHttpGet = new SimpleHttpGet();

        pageDownloader.writeToFile(simpleHttpGet, request);


    }

}
