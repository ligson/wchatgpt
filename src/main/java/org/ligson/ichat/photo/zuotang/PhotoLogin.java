package org.ligson.ichat.photo.zuotang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.ligson.ichat.util.MyHttpClient;
import org.ligson.ichat.util.MyHttpClientResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PhotoLogin {

    @Autowired
    private MyHttpClient myHttpClient;

    public Map<String, String> login() throws IOException {
        CloseableHttpClient httpClient = myHttpClient.getHttpClient();

        String url = "https://awpp.aoscdn.com/base/passport/v1/api/login";
        HttpPost httpPost = new HttpPost(url);
        HashMap<String, Object> kvHashMap = new HashMap<>();
        kvHashMap.put("brand_id", 29);
        kvHashMap.put("app_id", 363);
        kvHashMap.put("type", 27);
        kvHashMap.put("device_hash", "xxxwebdevicehashxxxxxxxxxxxxxxxx");
        kvHashMap.put("platform", 2);
        kvHashMap.put("os_name", "HONOR");
        kvHashMap.put("os_version", 9);
        kvHashMap.put("language", "zh");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(kvHashMap);
        httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        httpPost.addHeader("content-type", "application/json");
        String result = httpClient.execute(httpPost, new MyHttpClientResponseHandler());
        JsonNode jsonNode = objectMapper.readTree(result);
        String deviceId = jsonNode.get("data").get("device_id").asText();
        String apiToken = jsonNode.get("data").get("api_token").asText();
        Map<String, String> resMap = new HashMap<>();
        resMap.put("deviceId", deviceId);
        resMap.put("apiToken", apiToken);
        return resMap;
    }
}
