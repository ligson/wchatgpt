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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
            RegisterDTO userInfo = Constant.LOGIN_USER_MAP.get(req.getUsername());
            byte[] buffer = null;
            if (userInfo == null) {
                log.debug("test invoke....");
                buffer = "{\"code\":\"fail\",\"message\":\"没有检查到您的账号信息，请重新注册！\"}".getBytes(StandardCharsets.UTF_8);
            }
            if (userInfo != null && req.getPassword().equals(userInfo.getPassword())) {
                String token = UUID.randomUUID().toString();
                Constant.TOKEN_MAP.put(token, req.getUsername());
                buffer = "{\"code\":\"fail\",\"message\":\"您的密码错误，请重新输入！\"}".getBytes(StandardCharsets.UTF_8);
            }
            if (userInfo != null && req.getPassword().equals(userInfo.getPassword())) {
                String token = UUID.randomUUID().toString();
                Constant.TOKEN_MAP.put(token, req.getUsername());
                buffer = ("{\"code\":\"ok\",\"token\":\"" + token + "\",\"username\":\"" + userInfo.getUsername() + "\",\"nickname\":\"" + userInfo.getNickname() + "\"}").getBytes(StandardCharsets.UTF_8);
            }
            exchange.sendResponseHeaders(200, buffer.length);
            exchange.getResponseHeaders().add("Content-Type", "application/text;charset=UTF-8");
            exchange.getResponseBody().write(buffer);
            exchange.getResponseBody().close();
        }
    }
}
