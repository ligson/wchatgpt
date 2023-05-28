package org.ligson.ichat.photo.zuotang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.entity.NullEntity;
import org.ligson.ichat.util.MyHttpClient;
import org.ligson.ichat.util.MyHttpClientResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PhotoScaleGet {

    @Autowired
    private MyHttpClient myHttpClient;

    public Map<String, String> getImgUrl(String apiToken, String taskId) throws IOException {
        CloseableHttpClient httpClient = myHttpClient.getHttpClient();
        String url = "https://aw.aoscdn.com/app/picwish/tasks/anonymity/scale/" + taskId + "?product_id=482&language=zh";
        HttpGet httpPost = new HttpGet(url);
        ObjectMapper objectMapper = new ObjectMapper();
        httpPost.setEntity(NullEntity.INSTANCE);
        httpPost.addHeader("content-type", "application/json");
        httpPost.addHeader("authorization", "Bearer " + apiToken);
        String result = httpClient.execute(httpPost, new MyHttpClientResponseHandler());
        JsonNode jsonNode = objectMapper.readTree(result);
        String taskId2 = jsonNode.get("data").get("task_id").asText();
        String image = jsonNode.get("data").get("image").asText();
        String state = jsonNode.get("data").get("state").asText();
        String progress = jsonNode.get("data").get("progress").asText();
        Map<String, String> resMap = new HashMap<>();
        resMap.put("taskId", taskId2);
        resMap.put("state", state);
        resMap.put("image", image);
        resMap.put("progress", progress);
        return resMap;
    }
}
