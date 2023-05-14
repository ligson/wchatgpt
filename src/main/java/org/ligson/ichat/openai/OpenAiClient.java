package org.ligson.ichat.openai;


import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.ligson.ichat.ex.InnerException;
import org.ligson.ichat.openai.vo.req.ImgGenReq;
import org.ligson.ichat.openai.vo.res.AudioTranscriptionsRes;
import org.ligson.ichat.openai.vo.res.CompletionsRes;
import org.ligson.ichat.serializer.CruxSerializer;
import org.ligson.ichat.openai.vo.ModelResult;
import org.ligson.ichat.openai.vo.req.ChatCompletionsReq;
import org.ligson.ichat.openai.vo.req.CompletionsReq;
import org.ligson.ichat.openai.vo.res.ChatCompletionsRes;
import org.ligson.ichat.openai.vo.res.ImgGenRes;
import org.ligson.ichat.util.MyHttpClient;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class OpenAiClient {
    private final CruxSerializer serializer;

    private static final String BASE_URL = "https://api.openai.com";
    private final MyHttpClient myHttpClient;
    private final Header authHeader;
    private final Header jsonHeader = new BasicHeader("Content-Type", "application/json;charset=UTF-8");
    private final Header formDataHeader = new BasicHeader("Content-Type", ContentType.MULTIPART_FORM_DATA.getMimeType());

    public OpenAiClient(String skToken, MyHttpClient myHttpClient, CruxSerializer cruxSerializer) {
        this.serializer = cruxSerializer;
        this.myHttpClient = myHttpClient;
        authHeader = new BasicHeader("Authorization", "Bearer " + skToken);
    }

    public ModelResult models() {
        try {
            return myHttpClient.doGet(BASE_URL + "/v1/models", List.of(authHeader), ModelResult.class);
        } catch (Exception e) {
            log.error("获取模型失败:" + e.getMessage(), e);
            return null;
        }
    }

    public ChatCompletionsRes chatCompletions(ChatCompletionsReq req) {
        try {
            return myHttpClient.doPost(BASE_URL + "/v1/chat/completions", List.of(authHeader, jsonHeader), req, ChatCompletionsRes.class);
        } catch (Exception e) {
            log.error("请求问答失败:" + e.getMessage(), e);
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

    public ImgGenRes imageGenerations(ImgGenReq req) {
        try {
            return myHttpClient.doPost(BASE_URL + "/v1/images/generations", List.of(authHeader, jsonHeader), req, ImgGenRes.class);
        } catch (Exception e) {
            log.error("请求图形生成接口:" + e.getMessage(), e);
            return null;
        }
    }

    public String audioTranscriptions(File file) {
        log.debug("audio.....:{}",file.getAbsolutePath());
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.LEGACY)
                .addTextBody("model", "whisper-1")
                .addBinaryBody("file", file)
//                .addTextBody("response_format", "text")
//                .setContentType(ContentType.MULTIPART_FORM_DATA)
                .build();
        try {
            String json = myHttpClient.doPost(BASE_URL + "/v1/audio/transcriptions", List.of(authHeader, formDataHeader), httpEntity);
            AudioTranscriptionsRes res = serializer.deserialize(json, AudioTranscriptionsRes.class);
            return res.getText();
        } catch (Exception e) {
            throw new InnerException(e);
        }
    }
}
