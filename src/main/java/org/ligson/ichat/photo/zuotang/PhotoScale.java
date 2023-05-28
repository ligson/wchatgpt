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
public class PhotoScale {

    @Autowired
    private MyHttpClient myHttpClient;

    public Map<String, String> scale(String authorization, String resourceId) throws IOException {
        CloseableHttpClient httpClient = myHttpClient.getHttpClient();
        String url = "https://aw.aoscdn.com/app/picwish/tasks/anonymity/scale?product_id=482&language=zh";
        HttpPost httpPost = new HttpPost(url);
        HashMap<String, Object> kvHashMap = new HashMap<>();
        kvHashMap.put("website", "zh");
        kvHashMap.put("source_resource_id", resourceId);
        kvHashMap.put("resource_id", resourceId);
        kvHashMap.put("type", 2);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(kvHashMap);
        httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        httpPost.addHeader("content-type", "application/json");
        httpPost.addHeader("authorization", "Bearer " + authorization);
        String result = httpClient.execute(httpPost, new MyHttpClientResponseHandler());
        JsonNode jsonNode = objectMapper.readTree(result);
        String taskId = jsonNode.get("data").get("task_id").asText();
        String state = jsonNode.get("data").get("state").asText();
        String error = jsonNode.get("data").get("error").asText();
        Map<String, String> resMap = new HashMap<>();
        resMap.put("taskId", taskId);
        resMap.put("state", state);
        resMap.put("error", error);
        return resMap;
    }
}
