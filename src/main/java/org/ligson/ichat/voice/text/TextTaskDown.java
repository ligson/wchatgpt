package org.ligson.ichat.voice.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ligson.ichat.voice.audio.TaskDownVo;
import org.ligson.ichat.voice.audio.TextAudioCommon;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TextTaskDown {

    public static void main(String[] args) throws UnsupportedEncodingException, JsonProcessingException {
        String tasktag = "08d73794d7a3499e82fe963f94a86b41";
        taskdown(tasktag);

    }

    public static TaskDownVo taskdown(String tasktag) throws JsonProcessingException {
        String endTag = "hUuPd20171206LuOnD";
        TextContent4 content = new TextContent4();
        content.setDeviceid("43d8dec164e64699ba3208960ff5cc5c");
        content.setProductinfo("04D83019EE0348F9B439414D9CF0809BF0DDB8F032DFD0B9120BF5D98C12B110AAFAEA1BC0CC792D");
        content.setTasktag(tasktag);
        content.setDowntype(2);
        content.setTimestamp((int) (System.currentTimeMillis() / 1000));

        Map<String, Object> map = new HashMap<>();
        map.put("deviceid", content.getDeviceid());
        map.put("productinfo", content.getProductinfo());
        map.put("tasktag", content.getTasktag());
        map.put("downtype", content.getDowntype());
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
        HttpEntity<TextContent4> request = new HttpEntity<>(content, headers);
        String url = "https://app.xunjiepdf.com/api/v4/taskdown";
        TaskDownVo tokenVO = restTemplate.postForObject(url, request, TaskDownVo.class);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(tokenVO));
        return tokenVO;

    }
}
