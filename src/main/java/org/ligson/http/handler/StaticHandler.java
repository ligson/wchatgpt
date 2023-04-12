package org.ligson.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.ligson.fw.annotation.BootService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@BootService
@Slf4j
public class StaticHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();
        URL fileUrl = Thread.currentThread().getContextClassLoader().getResource("." + uri);
        if (fileUrl != null) {
            byte[] bytes = IOUtils.toByteArray(fileUrl);
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        } else {
            String response = "File not found";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }


        byte[] buffer = "ok".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, buffer.length);
        exchange.getResponseHeaders().add("Content-Type", "application/text;charset=UTF-8");
        exchange.getResponseBody().write(buffer);
        exchange.getResponseBody().close();
    }
}
