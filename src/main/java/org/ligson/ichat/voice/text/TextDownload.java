package org.ligson.ichat.voice.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ligson.ichat.voice.audio.TaskDownVo;
import org.ligson.ichat.voice.audio.TextAudioCommon;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TextDownload {

    public static void main(String[] args) throws UnsupportedEncodingException, JsonProcessingException {
        String url = "http://qcloudcos.xunjiepdf.com/xunjiepdf/temp/20230502/08d73794d7a3499e82fe963f94a86b41/%e5%9c%a8%e7%ba%bf%e7%89%88%e5%85%a8%e8%83%bd_31507.zh.txt";
        download(url);

    }

    public static String download(String url) {
        RestTemplate restTemplate = new RestTemplate();
        String entity = restTemplate.getForObject(URLDecoder.decode(url, StandardCharsets.UTF_8), String.class);
        assert entity != null;
        String res = new String(entity.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        System.out.println(res);
        return res;

    }
}

