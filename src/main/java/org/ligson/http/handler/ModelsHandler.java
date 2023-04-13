package org.ligson.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.ligson.chat.impl.OpenAIChatServiceImpl;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.serializer.CruxSerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@BootService
public class ModelsHandler implements HttpHandler {
    @BootAutowired
    private CruxSerializer cruxSerializer;
    @BootAutowired
    private OpenAIChatServiceImpl openAIChatService;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("data", openAIChatService.models());
        exchange.getResponseHeaders().set("ContentType", "application/json");
        byte[] replyBuf = cruxSerializer.serialize(map).getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, replyBuf.length);
        exchange.getResponseBody().write(replyBuf);
    }
}
