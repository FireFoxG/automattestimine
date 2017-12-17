package api.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Optional;

// Sources from here: https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java

public class JsonReceiver {
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        URL serviceUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();
        if(connection.getContentLength() > 0) {
            InputStream is = connection.getResponseCode() < 300 ? connection.getInputStream() : connection.getErrorStream();

            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }
        return new JSONObject();
    }
}
