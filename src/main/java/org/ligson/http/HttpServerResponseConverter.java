package org.ligson.http;

import com.sun.net.httpserver.HttpExchange;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.serializer.CruxSerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@BootService
public class HttpServerResponseConverter {
    @BootAutowired
    private CruxSerializer cruxSerializer;

    public void processResult(Map<String, Object> result, HttpExchange exchange) throws IOException {
        String retVal = cruxSerializer.serialize(result);
        byte[] buffer = retVal.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, buffer.length);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=UTF-8");
        exchange.getResponseBody().write(buffer);
        exchange.getResponseBody().close();
    }
}
