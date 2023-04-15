package org.ligson.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.http.HttpServerResponseConverter;
import org.ligson.http.ServerUserContext;
import org.ligson.serializer.CruxSerializer;
import org.ligson.vo.TokenDTO;
import org.ligson.vo.UserInfoVo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@BootService
public class CheckLoginHandler implements HttpHandler {

    @BootAutowired
    private CruxSerializer cruxSerializer;

    @BootAutowired
    private HttpServerResponseConverter httpServerResponseConverter;
    @BootAutowired
    private ServerUserContext serverUserContext;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            TokenDTO tokenDTO = cruxSerializer.deserialize(IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8), TokenDTO.class);
            if (tokenDTO != null) {
                UserInfoVo userInfo = serverUserContext.getLoginUserByToken(tokenDTO.getToken());
                if (userInfo != null) {
                    result.put("success", true);
                    result.put("username", userInfo.getUsername());
                    result.put("token", tokenDTO.getToken());
                    httpServerResponseConverter.processResult(result, exchange);
                    return;
                }
            }

            result.put("success", false);
            result.put("msg", "登录已经过期");
            httpServerResponseConverter.processResult(result, exchange);
        } else {
            result.put("success", false);
            result.put("msg", "格式错误!");
            httpServerResponseConverter.processResult(result, exchange);
        }
    }
}
