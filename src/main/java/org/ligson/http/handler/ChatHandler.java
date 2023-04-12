package org.ligson.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import org.ligson.chat.impl.OpenAIChatServiceImpl;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.openai.vo.req.CompletionsReq;
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

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        CompletionsReq req = cruxSerializer.deserialize(IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8), CompletionsReq.class);
        String msg = openAIChatService.chat(req);
        Map<String, Object> result = new HashMap<>();
        if (msg != null) {
            result.put("success", true);
            result.put("msg", msg);

        } else {
            result.put("success", false);
        }
        exchange.getResponseHeaders().set("ContentType", "application/json");
        byte[] replyBuf = cruxSerializer.serialize(result).getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, replyBuf.length);
        exchange.getResponseBody().write(replyBuf);
    }
}
