package org.ligson.ichat.photo.zuotang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.ligson.ichat.util.MyHttpClient;
import org.ligson.ichat.util.MyHttpClientResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PhotoUpload {

    @Autowired
    private MyHttpClient myHttpClient;

    public Map<String, String> upload(String authorization, String objectName, String ossCallBack, String ossDate, String securityToken, String localImgPath) throws IOException {
        CloseableHttpClient httpClient = myHttpClient.getHttpClient();
        String url = "https://picwishsz.oss-cn-shenzhen.aliyuncs.com/" + objectName;
        HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(new FileEntity(new File(localImgPath), ContentType.IMAGE_JPEG));
        String png = objectName.substring(objectName.lastIndexOf(".") + 1);
        httpPut.addHeader("accept", "application/json");
        httpPut.addHeader("content-type", "image/" + png);
        httpPut.addHeader("authorization", authorization);
        httpPut.addHeader("x-oss-callback", ossCallBack);
        httpPut.addHeader("x-oss-date", ossDate);
        httpPut.addHeader("x-oss-security-token", securityToken);
        String result = httpClient.execute(httpPut, new MyHttpClientResponseHandler());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result);
        String filename = jsonNode.get("data").get("filename").asText();
        String height = jsonNode.get("data").get("height").asText();
        String resourceId = jsonNode.get("data").get("resource_id").asText();
        String size = jsonNode.get("data").get("size").asText();
        String type = jsonNode.get("data").get("type").asText();
        String url2 = jsonNode.get("data").get("url").asText();
        String width = jsonNode.get("data").get("width").asText();
        Map<String, String> resMap = new HashMap<>();
        resMap.put("filename", filename);
        resMap.put("height", height);
        resMap.put("resourceId", resourceId);
        resMap.put("size", size);
        resMap.put("type", type);
        resMap.put("url2", url2);
        resMap.put("width", width);
        return resMap;
    }
}
