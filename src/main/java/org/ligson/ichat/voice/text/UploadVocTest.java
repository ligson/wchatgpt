package org.ligson.ichat.voice.text;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ligson.ichat.voice.audio.TokenVO;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class UploadVocTest {

    public static void main(String[] args) throws IOException {
        String tasktag = "5d42e72313b2412696da0a1b3c4c37d5";
        int timestamp = 1683021788;
        String tasktoken = "bf3bdd206f870fb6495815998164f2c1";
        String filePath = "/Users/jinmancang1/Downloads/在线版全能_31507.mp3";
        uploadFile(filePath, tasktag, tasktoken, timestamp);

    }

    public static TokenVO uploadFile(String filePath, String tasktag, String tasktoken, int timestamp) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        params.add("partFile", fileSystemResource);
        headers.setContentLength(fileSystemResource.contentLength());
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
        String url = "https://app.xunjiepdf.com/api/v4/uploadfile?tasktag=" + tasktag + "&timestamp=" + timestamp + "&tasktoken=" + tasktoken + "&fileindex=0&chunks=1&chunk=0";
//        String url = "https://app.xunjiepdf.com/api/v4/uploadfile?tasktag=b6a54da3a43a4b648e24a988ccbd44f6&timestamp=1683023493&tasktoken=a18436db487207078d87ae38acf9232f&fileindex=0&chunks=1&chunk=0";
        TokenVO tokenVO = restTemplate.postForObject(url, request, TokenVO.class);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(tokenVO));
        return tokenVO;
    }

}