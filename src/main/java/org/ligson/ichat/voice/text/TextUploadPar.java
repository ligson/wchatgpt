package org.ligson.ichat.voice.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ligson.ichat.voice.audio.TextAudioCommon;
import org.ligson.ichat.voice.audio.TokenVO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TextUploadPar {

    public static void main(String[] args) throws UnsupportedEncodingException, JsonProcessingException {
        String filename = "在线版全能_31507.mp3";
        uploadpar(filename);

    }

    public static TokenVO uploadpar(String filename) throws JsonProcessingException {
        String endTag = "hUuPd20171206LuOnD";
        TextContent content = new TextContent();
        content.setFilename(filename);
        content.setFilecount(1);
        content.setSoftname("PDFConverterAssistantWeb");
        content.setSoftversion("V1.0.0.0");
        content.setMachineid("43d8dec164e64699ba3208960ff5cc5c");
        content.setProductid(544);
        content.setTasktype("voicefanyi");
        content.setLimitsize(2048);
        content.setIsshare(0);
        content.setOutputfileextension("txt");
        content.setFanyi_from("zh-CHS");
        content.setFanyi_to("zh-CHS");
        content.setPagerange("all");
        content.setProductinfo("");
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
        map.put("fanyi_from", content.getFanyi_from());
        map.put("fanyi_to", content.getFanyi_to());
        map.put("pagerange", content.getPagerange());
        map.put("timestamp", content.getTimestamp());

        List<String> keys = map.keySet().stream().sorted().collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            Object value = map.get(key);
            sb.append(key).append("=").append(value).append("&");
        }
        sb.deleteCharAt(sb.length() - 1).append(endTag);
        String x = sb.toString();
        System.out.println(x);
        String unUrl = TextAudioCommon.unescape(TextAudioCommon.encodeURIComponent(x));
        String dataSign = TextAudioCommon.getDataSign(unUrl);
        content.setDatasign(dataSign);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TextContent> request = new HttpEntity<>(content, headers);
        String url = "https://app.xunjiepdf.com/api/v4/uploadpar";
        TokenVO tokenVO = restTemplate.postForObject(url, request, TokenVO.class);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(tokenVO));
        return tokenVO;
    }

}