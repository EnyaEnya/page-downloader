import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {

        PageDownloader pageDownloader = new PageDownloader();

        pageDownloader.download("https://yandex.ru/");


    }

}
