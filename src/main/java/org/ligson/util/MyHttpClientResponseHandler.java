package org.ligson.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MyHttpClientResponseHandler implements HttpClientResponseHandler<String> {
    @Override
    public String handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
        String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        EntityUtils.consumeQuietly(response.getEntity());
        log.debug("http code:{},result:{}", response.getCode(), result);
        if (response.getCode() != HttpStatus.SC_OK) {
            log.error("http request error:{}", response.getReasonPhrase());
            return null;
        }
        return result;
    }
}
