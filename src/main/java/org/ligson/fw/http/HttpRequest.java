package org.ligson.fw.http;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class HttpRequest {
    private String method;
    private String path;
    private String protocolVersion;
    private Map<String, HttpHeader> headerMap = new ConcurrentHashMap<>();
    private byte[] body = new byte[0];

    public void putHeader(String name, String value) {
        HttpHeader header = new HttpHeader();
        header.setName(name);
        header.setValue(value);
        headerMap.put(name, header);
    }

    public HttpHeader getHeader(String name) {
        return headerMap.get(name);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return this.body;
    }
}
