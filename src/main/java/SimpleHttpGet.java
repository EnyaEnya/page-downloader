import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SimpleHttpGet {

    public String sendHttpGet(HttpGet request) throws ClientProtocolException, IOException {
        org.apache.http.client.HttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        String result = "";
        while ((line = rd.readLine()) != null) {
            result += line;
        }
        return result;
    }
}
