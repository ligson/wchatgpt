package org.ligson.ichat.photo.qingtu;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.Header;
import org.ligson.ichat.util.MyHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class QingTuToken {

    @Autowired
    private MyHttpClient myHttpClient;

    public Map<String, String> getToken() throws IOException {
        CloseableHttpClient httpClient = myHttpClient.getHttpClient();
        String url = "https://qingtu.cn/";
        HttpGet httpGet = new HttpGet(url);
        Map<String, String> resMap = new HashMap<>();
        httpClient.execute(httpGet, (response) -> {
            Header[] headers = response.getHeaders();
            for (Header header : headers) {
                if (header.getValue().contains("XSRF-TOKEN")) {
                    resMap.put("cookie", header.getValue().split(";")[0]);
                    break;
                }
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("qingtu_token")) {
                    System.out.println(line);
                    resMap.put("X-CSRF-TOKEN", line.split("\"")[1]);
                }
            }
            return null;
        });
        return resMap;
    }
}
