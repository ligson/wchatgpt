package org.ligson.ichat.photo.qingtu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.ligson.ichat.util.MyHttpClient;
import org.ligson.ichat.util.MyHttpClientResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class QingTuUpload {

    @Autowired
    private MyHttpClient myHttpClient;

    public Map<String, String> upload(String imageUrl, String cookie, String xCsrfToken) throws IOException {
        CloseableHttpClient httpClient = myHttpClient.getHttpClient();
        String url = "https://qingtu.cn/qingtu/upload";
        HttpPost httpPost = new HttpPost(url);
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addPart("file0", new FileBody(new File(imageUrl)))
                .setContentType(ContentType.MULTIPART_FORM_DATA)
                .build();
        httpPost.setEntity(httpEntity);
        httpPost.addHeader(new BasicHeader("X-CSRF-TOKEN", xCsrfToken));
        httpPost.addHeader(new BasicHeader("Cookie", cookie));
        String result = httpClient.execute(httpPost, new MyHttpClientResponseHandler());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result);
        String code = jsonNode.get("code").asText();
        String msg = jsonNode.get("msg").asText();
        String hashid = jsonNode.get("hashid").asText();
        Map<String, String> resMap = new HashMap<>();
        System.out.println(result);
        resMap.put("hashid", hashid);
        return resMap;
    }
}
