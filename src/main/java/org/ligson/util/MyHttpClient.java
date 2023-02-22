package org.ligson.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.ligson.serializer.CruxSerializer;
import org.ligson.serializer.jackson.JacksonSerializer;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MyHttpClient {
    private final CloseableHttpClient httpClient;
    private final CruxSerializer serializer;

    public MyHttpClient() {
        this(Timeout.of(60, TimeUnit.SECONDS), Timeout.of(60, TimeUnit.SECONDS));
    }

    public MyHttpClient(Timeout connectTimeout, Timeout socketTimeout) {
        BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        connectionManager.setConnectionConfig(connectionConfig);
        httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        serializer = new JacksonSerializer();
    }

    private String doRequest(HttpUriRequestBase requestBase, List<Header> headers) throws Exception {
        headers.forEach(requestBase::addHeader);
        log.debug("http method:{},url:{},headers:{}", requestBase.getMethod(), requestBase.getUri(), headers);
        MyHttpClientResponseHandler myHttpClientResponseHandler = new MyHttpClientResponseHandler();
        return httpClient.execute(requestBase, myHttpClientResponseHandler);
    }

    public String doPost(String url, List<Header> headers, String body) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        log.debug("POST地址:{},请求body:{}", url, body);
        httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        return doRequest(httpPost, headers);
    }

    public <T> T doPost(String url, List<Header> headers, String body, Class<T> returnType) throws Exception {
        String result = doPost(url, headers, body);
        return deserialize(result, returnType);
    }

    public <T, E> T doPost(String url, List<Header> headers, E reqBody, Class<T> returnType) throws Exception {
        String result = doPost(url, headers, serializer.serialize(reqBody));
        return deserialize(result, returnType);
    }

    public String doGet(String url, List<Header> headers) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        return doRequest(httpGet, headers);
    }

    public <T> T doGet(String url, List<Header> headers, Class<T> returnType) throws Exception {
        String result = doGet(url, headers);
        return deserialize(result, returnType);
    }

    private <T> T deserialize(String result, Class<T> returnType) {
        return serializer.deserialize(result, returnType);
    }

}
