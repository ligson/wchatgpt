package org.ligson.turing;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ligson.serializer.CruxSerializer;
import org.ligson.turing.vo.req.TuringReq;
import org.ligson.turing.vo.res.TuringResult;
import org.ligson.util.MyHttpClient;

import java.util.Collections;

@Data
@Slf4j
public class TuringClient {
    private String userId;
    private String apiKey;
    private MyHttpClient myHttpClient;
    private static final String baseUrl = "http://openapi.turingapi.com/openapi/api/v2";
    private final CruxSerializer serializer;

    public TuringClient(String userId, String apiKey, MyHttpClient myHttpClient, CruxSerializer serializer) {
        this.apiKey = apiKey;
        this.userId = userId;
        this.myHttpClient = myHttpClient;
        this.serializer = serializer;
    }

    public TuringResult chat(String msg) {
        TuringReq req = TuringReq.create(userId, apiKey, msg);
        try {
            return myHttpClient.doPost(baseUrl, Collections.emptyList(), req, TuringResult.class);
        } catch (Exception e) {
            log.error("调用图灵失败:" + e.getMessage(), e);
            return null;
        }
    }
}
