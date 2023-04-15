package org.ligson.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.ligson.constant.Constant;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.http.HttpServerResponseConverter;
import org.ligson.serializer.CruxSerializer;
import org.ligson.vo.LoginDTO;
import org.ligson.vo.RegisterDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@BootService
public class LoginHandler implements HttpHandler {

    @BootAutowired
    private CruxSerializer cruxSerializer;
    @BootAutowired
    private HttpServerResponseConverter httpServerResponseConverter;


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            LoginDTO req = cruxSerializer.deserialize(IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8), LoginDTO.class);
            RegisterDTO userInfo = Constant.LOGIN_USER_MAP.get(req.getUsername());
            if (userInfo == null) {
                result.put("success", false);
                result.put("msg", "没有检查到您的账号信息，请联系管理员！");
                httpServerResponseConverter.processResult(result, exchange);
                return;
            }
            if (req.getPassword().equals(userInfo.getPassword())) {
                String token = UUID.randomUUID().toString();
                Constant.TOKEN_MAP.put(token, req.getUsername());
                result.put("success", true);
                result.put("msg", "没有检查到您的账号信息，请联系管理员！");
                result.put("token", token);
            } else {
                result.put("success", false);
                result.put("msg", "没有检查到您的账号信息,或者密码错误，请联系管理员！");
            }
            httpServerResponseConverter.processResult(result, exchange);
            return;
        }
        result.put("success", false);
        result.put("msg", "格式错误!");
        httpServerResponseConverter.processResult(result, exchange);
    }
}
