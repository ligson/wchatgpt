package org.ligson.fw.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpResponse {
    private Map<String, HttpHeader> httpHeaderMap = new ConcurrentHashMap<>();

    public void putHeader(String name, String value) {
        HttpHeader header = new HttpHeader(name, value);
        httpHeaderMap.put(name, header);
    }

    public void write(OutputStream outputStream, byte[] body) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 200 OK\r\n");
        httpHeaderMap.forEach((k, v) -> {
            if (!k.equals("Content-Length")) {
                builder.append(k).append(": ").append(v.getValue()).append("\r\n");
            }
        });
        builder.append("Content-Length: ").append(body.length).append("\r\n\r\n");
        System.out.println(builder.toString());
        outputStream.write(builder.toString().getBytes());
        outputStream.write(body);
    }
}
