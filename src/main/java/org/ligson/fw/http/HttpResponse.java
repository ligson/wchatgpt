package org.ligson.fw.http;

import org.ligson.fw.http.enums.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpResponse {
    private final Map<String, HttpHeader> httpHeaderMap = new ConcurrentHashMap<>();
    private HttpStatus httpStatus = HttpStatus.OK;

    private SelectionKey selectionKey;
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    private boolean firstWrite = false;

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public void putHeader(String name, String value) {
        HttpHeader header = new HttpHeader(name, value);
        httpHeaderMap.put(name, header);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    private byte[] buildHeader(byte[] body) {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ").append(httpStatus.toString()).append("\r\n");
        httpHeaderMap.forEach((k, v) -> {
            if (!k.equals("Content-Length")) {
                builder.append(k).append(": ").append(v.getValue()).append("\r\n");
            }
        });
        builder.append("Content-Length: ").append(body.length).append("\r\n\r\n");
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void flush() throws IOException {
        if (selectionKey == null) {
            return;
        }
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        byte[] buffer = bos.toByteArray();
        byte[] headerBytes = buildHeader(buffer);
        socketChannel.write(ByteBuffer.wrap(headerBytes));
        socketChannel.write(ByteBuffer.wrap(buffer));
    }

    public void write(byte[] body) throws IOException {
        if (selectionKey == null) {
            throw new IOException("请先设置selectionKey");
        }
        if (!firstWrite) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            socketChannel.register(selectionKey.selector(), SelectionKey.OP_WRITE);
            firstWrite = true;
        }
        bos.write(body);
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
