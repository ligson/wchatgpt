package org.ligson.http.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import org.ligson.chat.impl.OpenAIChatServiceImpl;
import org.ligson.constant.Constant;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.http.HttpServerResponseConverter;
import org.ligson.openai.vo.req.ChatCompletionsReq;
import org.ligson.serializer.CruxSerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@BootService
public class ChatHandler implements HttpHandler {
    @BootAutowired
    private CruxSerializer cruxSerializer;
    @BootAutowired
    private OpenAIChatServiceImpl openAIChatService;
    @BootAutowired
    private HttpServerResponseConverter httpServerResponseConverter;


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        Headers requestHeaders = exchange.getRequestHeaders();
        if (requestHeaders.get("token") == null || Constant.TOKEN_MAP.get(requestHeaders.get("token").get(0)) == null) {
            result.put("success", true);
            result.put("msg", "您的登录会话已经过期，请重新登录!");
            httpServerResponseConverter.processResult(result, exchange);
            return;
        }
        ChatCompletionsReq req = cruxSerializer.deserialize(IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8), ChatCompletionsReq.class);
        String msg = openAIChatService.chat(req);
        if (msg != null) {
            result.put("success", true);
            result.put("msg", msg);
        } else {
            result.put("success", false);
        }
        httpServerResponseConverter.processResult(result, exchange);
    }
}
