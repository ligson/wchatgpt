package org.ligson.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.ligson.fw.annotation.BootService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@BootService
@Slf4j
public class TestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("get".equalsIgnoreCase(exchange.getRequestMethod())) {
            log.debug("test invoke....");
            byte[] buffer = "ok".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, buffer.length);
            exchange.getResponseHeaders().add("Content-Type", "application/text;charset=UTF-8");
            exchange.getResponseBody().write(buffer);
            exchange.getResponseBody().close();
        }
    }
}
