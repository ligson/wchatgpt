package org.ligson.openai;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicHeader;
import org.ligson.openai.vo.res.CompletionsRes;
import org.ligson.serializer.CruxSerializer;
import org.ligson.serializer.jackson.JacksonSerializer;
import org.ligson.util.MyHttpClient;
import org.ligson.openai.vo.req.CompletionsReq;
import org.ligson.openai.vo.ModelResult;

import java.util.List;

@Slf4j
public class OpenAiClient {
    private final CruxSerializer serializer;

    private static final String BASE_URL = "https://api.openai.com";
    private final MyHttpClient myHttpClient;
    private final Header authHeader;
    private final Header jsonHeader = new BasicHeader("Content-Type", "application/json;charset=UTF-8");

    public OpenAiClient(String skToken) {
        myHttpClient = new MyHttpClient();
        authHeader = new BasicHeader("Authorization", "Bearer " + skToken);
        serializer = new JacksonSerializer();
    }

    public ModelResult models() {
        try {
            return myHttpClient.doGet(BASE_URL + "/v1/models", List.of(authHeader), ModelResult.class);
        } catch (Exception e) {
            log.error("获取模型失败:" + e.getMessage(), e);
            return null;
        }
    }

    public CompletionsRes completions(CompletionsReq req) {
        try {
            return myHttpClient.doPost(BASE_URL + "/v1/completions", List.of(authHeader, jsonHeader), req, CompletionsRes.class);
        } catch (Exception e) {
            log.error("请求问答失败:" + e.getMessage(), e);
            return null;
        }
    }
}
