package org.ligson.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.http.MsgTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@BootService
@Slf4j
public class MsgHandler implements HttpHandler {
    @BootAutowired
    private MsgTemplate msgTemplate;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        log.debug("请求path:{}", path);
        //"/msg/user/sd"
        String[] arr = path.split("/");
        if (arr.length != 4) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            return;
        }
        String msgId = arr[3];
        String userId = arr[2];
        String html = msgTemplate.getMsgHtml(userId, msgId);
        if (StringUtils.isBlank(html)) {
            html = "<p>消息还未生成</p>";
        }
        byte[] buffer = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html;charset=UTF-8");
        exchange.sendResponseHeaders(200, buffer.length);
        exchange.getResponseBody().write(buffer);
        exchange.close();
    }
}
