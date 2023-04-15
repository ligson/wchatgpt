package org.ligson.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.ligson.constant.Constant;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.serializer.CruxSerializer;
import org.ligson.vo.LoginDTO;
import org.ligson.vo.RegisterDTO;
import org.ligson.vo.TokenDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@BootService
public class CheckLoginHandler implements HttpHandler {

    @BootAutowired
    private CruxSerializer cruxSerializer;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            TokenDTO tokenDTO = cruxSerializer.deserialize(IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8), TokenDTO.class);
            if (tokenDTO!=null && Constant.TOKEN_MAP.get(tokenDTO.getToken()) != null) {
                RegisterDTO userInfo = Constant.LOGIN_USER_MAP.get(Constant.TOKEN_MAP.get(tokenDTO.getToken()));
                byte[] buffer = ("{\"code\":\"ok\",\"token\":\"" + tokenDTO.getToken() + "\",\"username\":\"" + userInfo.getUsername() + "\",\"nickname\":\"" + userInfo.getNickname() + "\"}").getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, buffer.length);
                exchange.getResponseHeaders().add("Content-Type", "application/text;charset=UTF-8");
                exchange.getResponseBody().write(buffer);
                exchange.getResponseBody().close();
                return;
            }
            log.debug("test invoke....");
            byte[] buffer = "{\"code\":\"fail\"}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, buffer.length);
            exchange.getResponseHeaders().add("Content-Type", "application/text;charset=UTF-8");
            exchange.getResponseBody().write(buffer);
            exchange.getResponseBody().close();
        }
    }
}
