package org.ligson.fw.http;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Data
public class HttpRequestParser {
    private InputStream inputStream;

    public HttpRequestParser(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HttpRequest parse() throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            byte[] lineBuffer = new byte[len];
            System.arraycopy(buffer, 0, lineBuffer, 0, len);
            bos.write(lineBuffer);
            if (len < buffer.length) {
                break;
            }
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bos.toByteArray())));
        String firstLine = reader.readLine();
        if (firstLine == null) {
            return null;
        }
        String[] arr = firstLine.split(" ");
        if (arr.length != 3) {
            throw new IOException("Http请求头格式错误:" + firstLine);
        }
        String method = arr[0];
        String path = arr[1];
        String protocolVersion = arr[2];
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod(method.toUpperCase());
        httpRequest.setPath(path);
        httpRequest.setProtocolVersion(protocolVersion);
        //https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println("---" + line + "---");
            if (!"".equals(line)) {
                String[] arr2 = line.split(": ");
                if (arr2.length == 2) {
                    httpRequest.putHeader(arr2[0], arr2[1]);
                }
            } else {
                HttpHeader contentLength = httpRequest.getHeader("Content-Length");
                if (contentLength != null && StringUtils.isNoneBlank(contentLength.getValue())) {
                    int bodyLen = Integer.parseInt(contentLength.getValue().trim());
                    byte[] body = new byte[bodyLen];
                    for (int i = 0; i < bodyLen; i++) {
                        body[i] = (byte) reader.read();
                    }
                    httpRequest.setBody(body);
                }
            }
        }
        reader.close();
        return httpRequest;
    }

}
