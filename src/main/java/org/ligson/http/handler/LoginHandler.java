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
import org.ligson.vo.LoginDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@BootService
public class LoginHandler implements HttpHandler {

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
            LoginDTO req = cruxSerializer.deserialize(IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8), LoginDTO.class);
            httpServerResponseConverter.processResult(serverUserContext.login(req.getUsername(), req.getPassword()), exchange);
            return;
        }
        result.put("success", false);
        result.put("msg", "格式错误!");
        httpServerResponseConverter.processResult(result, exchange);
    }
}
