package org.ligson.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.ligson.serializer.CruxSerializer;
import org.ligson.serializer.jackson.JacksonSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MyHttpClient {
    private final CloseableHttpClient httpClient;
    private final CruxSerializer serializer;

    public MyHttpClient() {
        this(Timeout.of(200, TimeUnit.SECONDS), Timeout.of(300, TimeUnit.SECONDS));
    }

    public MyHttpClient(Timeout connectTimeout, Timeout socketTimeout) {
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(200)
                .setMaxConnPerRoute(100)
                .setDefaultConnectionConfig(connectionConfig)
                .build();
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

    public File download(String url, String fileName, String destDir) throws IOException {
        HttpGet get = new HttpGet(url);
        MyFileHttpClientResponseHandler myHttpClientResponseHandler = new MyFileHttpClientResponseHandler();
        File file = httpClient.execute(get, myHttpClientResponseHandler);
        if (file == null) {
            log.error("下载url:{}到:{}失败", url, destDir + "/" + fileName);
            return null;
        } else {
            String ext = FilenameUtils.getExtension(file.getName());
            FileInputStream fis = new FileInputStream(file);
            File destDirFile = new File(destDir);
            if (!destDirFile.exists()) {
                destDirFile.mkdirs();
            }
            File destFile = new File(destDir, fileName + "." + ext);
            if (!destFile.exists()) {
                destFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(destFile);
            IOUtils.copy(fis, fos);
            fis.close();
            fos.close();
            return destFile;
        }
    }

}
