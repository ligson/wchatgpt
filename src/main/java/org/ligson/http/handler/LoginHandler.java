package org.ligson.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.ligson.constant.Constant;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.openai.vo.req.ChatCompletionsReq;
import org.ligson.serializer.CruxSerializer;
import org.ligson.vo.LoginDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@BootService
public class LoginHandler implements HttpHandler {

    @BootAutowired
    private CruxSerializer cruxSerializer;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            LoginDTO req = cruxSerializer.deserialize(IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8), LoginDTO.class);
            String password = Constant.LOGIN_USER_MAP.get(req.getUsername());
            if (req.getPassword().equals(password)) {
                String token = UUID.randomUUID().toString();
                Constant.TOKEN_MAP.put(token, req.getUsername());
                byte[] buffer = "ok".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, buffer.length);
                exchange.getResponseHeaders().add("token", token);
                exchange.getResponseHeaders().add("Content-Type", "application/text;charset=UTF-8");
                exchange.getResponseBody().write(buffer);
                exchange.getResponseBody().close();
                return;
            }
            log.debug("test invoke....");
            byte[] buffer = "fail".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, buffer.length);
            exchange.getResponseHeaders().add("Content-Type", "application/text;charset=UTF-8");
            exchange.getResponseBody().write(buffer);
            exchange.getResponseBody().close();
        }
    }
}
