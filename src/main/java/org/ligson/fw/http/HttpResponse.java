package org.ligson.fw.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpResponse {
    private Map<String, HttpHeader> httpHeaderMap = new ConcurrentHashMap<>();

    public void putHeader(String name, String value) {
        HttpHeader header = new HttpHeader(name, value);
        httpHeaderMap.put(name, header);
    }

    private byte[] buildHeader(byte[] body) {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 200 OK\r\n");
        httpHeaderMap.forEach((k, v) -> {
            if (!k.equals("Content-Length")) {
                builder.append(k).append(": ").append(v.getValue()).append("\r\n");
            }
        });
        builder.append("Content-Length: ").append(body.length).append("\r\n\r\n");
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void write(OutputStream outputStream, byte[] body) throws IOException {
        byte[] headerBytes = buildHeader(body);
        outputStream.write(headerBytes);
        outputStream.write(body);
    }

    public void write(SocketChannel socketChannel, byte[] body) throws IOException {
        byte[] headerBytes = buildHeader(body);
        socketChannel.write(ByteBuffer.wrap(headerBytes));
        socketChannel.write(ByteBuffer.wrap(body));
    }
}
