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
public class PhotoOss {

    @Autowired
    private MyHttpClient myHttpClient;

    public Map<String, String> oss(String authorization, String imageName) throws IOException {
        CloseableHttpClient httpClient = myHttpClient.getHttpClient();
        String url = "https://aw.aoscdn.com/app/picwish/authorizations/oss?product_id=482&language=zh";
        HttpPost httpPost = new HttpPost(url);
        HashMap<String, Object> kvHashMap = new HashMap<>();
        kvHashMap.put("filenames", new String[]{imageName});
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(kvHashMap);
        httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        httpPost.addHeader("content-type", "application/json");
        httpPost.addHeader("authorization", "Bearer " + authorization);
        String result = httpClient.execute(httpPost, new MyHttpClientResponseHandler());
        JsonNode jsonNode = objectMapper.readTree(result);
        String accessKeySecret = jsonNode.get("data").get("credential").get("access_key_secret").asText();
        String accessKeyId = jsonNode.get("data").get("credential").get("access_key_id").asText();
        String securityToken = jsonNode.get("data").get("credential").get("security_token").asText();
        String bucket = jsonNode.get("data").get("bucket").asText();
        String region = jsonNode.get("data").get("region").asText();
        String endpoint = jsonNode.get("data").get("endpoint").asText();
        String imageObj = jsonNode.get("data").get("objects").get(imageName).asText();
        Map<String, String> resMap = new HashMap<>();
        resMap.put("accessKeySecret", accessKeySecret);
        resMap.put("accessKeyId", accessKeyId);
        resMap.put("securityToken", securityToken);
        resMap.put("bucket", bucket);
        resMap.put("region", region);
        resMap.put("endpoint", endpoint);
        resMap.put("objectName", imageObj);
        return resMap;
    }
}
