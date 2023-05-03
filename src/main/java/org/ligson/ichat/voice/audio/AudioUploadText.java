package org.ligson.ichat.voice.audio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AudioUploadText {

    public static TokenVO uploadtext(String textvoice, String tasktag, String tasktoken,int timestamp) throws JsonProcessingException {
        String endTag = "hUuPd20171206LuOnD";
        AudioContent2 content = new AudioContent2();
        content.setDeviceid("43d8dec164e64699ba3208960ff5cc5c");
        content.setFileindex(0);
        content.setProductinfo("04D83019EE0348F9B439414D9CF0809BF0DDB8F032DFD0B9120BF5D98C12B110AAFAEA1BC0CC792D");
        content.setTasktag(tasktag);
        content.setTasktoken(tasktoken);
        content.setTextvoice(textvoice);
        content.setTimestamp(timestamp);

        Map<String, Object> map = new HashMap<>();
        map.put("deviceid", content.getDeviceid());
        map.put("fileindex", content.getFileindex());
        map.put("productinfo", content.getProductinfo());
        map.put("tasktag", content.getTasktag());
        map.put("tasktoken", content.getTasktoken());
        map.put("textvoice", content.getTextvoice());
        map.put("timestamp", content.getTimestamp());

        List<String> keys = map.keySet().stream().sorted().collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            Object value = map.get(key);
            sb.append(key).append("=").append(value).append("&");
        }
        sb.deleteCharAt(sb.length() - 1).append(endTag);
        String x = sb.toString();
        String unUrl = TextAudioCommon.unescape(TextAudioCommon.encodeURIComponent(x));
        String dataSign = TextAudioCommon.getDataSign(unUrl);
        content.setDatasign(dataSign);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AudioContent2> request = new HttpEntity<>(content, headers);
        String url = "https://app.xunjiepdf.com/api/v4/uploadtext";
        TokenVO tokenVO = restTemplate.postForObject(url, request, TokenVO.class);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(tokenVO));
        return tokenVO;
    }
}

