package Handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HttpSendRequest {

    public static String sendRequestGet(String query) throws Exception {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);

        URL urlObj = new URL(AppSetup.getPrometheusUrl() + encoded);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

        conn.setRequestProperty("Authorization", "Bearer " + AppSetup.getK3sToken());
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            return conn.getResponseMessage();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();

        return sb.toString();
    }

}
