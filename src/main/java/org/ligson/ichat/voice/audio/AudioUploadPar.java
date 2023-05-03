package org.ligson.ichat.voice.audio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AudioUploadPar {

    public static void main(String[] args) throws UnsupportedEncodingException, JsonProcessingException {
        String filename = "面试了啥地方黑色柳丁.txt";
        uploadpar(filename);

    }

    public static TokenVO uploadpar(String filename) throws JsonProcessingException {
        String endTag = "hUuPd20171206LuOnD";
        AudioContent content = new AudioContent();
        content.setFilecount(1);
        content.setFilename(filename);
        content.setIsshare(0);
        content.setLimitsize(2048);
        content.setMachineid("43d8dec164e64699ba3208960ff5cc5c");
        content.setOutputfileextension("mp3");
        content.setParainfo("volume:5;personselect:4;talkspeed:5;pitch:5");
        content.setProductid(544);
        content.setSoftname("PDFConverterAssistantWeb");
        content.setSoftversion("V1.0.0.0");
        content.setTasktype("text2mixaudio");
        content.setTimestamp((int) (System.currentTimeMillis() / 1000));

        Map<String, Object> map = new HashMap<>();
        map.put("filename", content.getFilename());
        map.put("filecount", content.getFilecount());
        map.put("softname", content.getSoftname());
        map.put("softversion", content.getSoftversion());
        map.put("machineid", content.getMachineid());
        map.put("productid", content.getProductid());
        map.put("tasktype", content.getTasktype());
        map.put("limitsize", content.getLimitsize());
        map.put("isshare", content.getIsshare());
        map.put("outputfileextension", content.getOutputfileextension());
        map.put("parainfo", content.getParainfo());
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
        HttpEntity<AudioContent> request = new HttpEntity<>(content, headers);
        String url = "https://app.xunjiepdf.com/api/v4/uploadpar";
        TokenVO tokenVO = restTemplate.postForObject(url, request, TokenVO.class);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(tokenVO));
        return tokenVO;
    }


}