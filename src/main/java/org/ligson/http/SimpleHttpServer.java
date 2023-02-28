package org.ligson.http;

import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.ligson.vo.AppConfig;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SimpleHttpServer {
    private HttpServer httpServer;
    private AppConfig appConfig;

    public SimpleHttpServer() throws Exception {
        appConfig = AppConfig.getInstance();
        httpServer = HttpServer.create(new InetSocketAddress(appConfig.getApp().getServer().getPort()), 100);
        httpServer.createContext("/test", exchange -> {
            if ("get".equalsIgnoreCase(exchange.getRequestMethod())) {
                log.debug("test invoke....");
                byte[] buffer = "ok".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, buffer.length);
                exchange.getResponseHeaders().add("Content-Type", "application/text;charset=UTF-8");
                exchange.getResponseBody().write(buffer);
                exchange.getResponseBody().close();
            }
        });

        WxHandler wxHandler = new WxHandler();
        httpServer.createContext("/auth", wxHandler);
        MsgHandler msgHandler  = new MsgHandler();
        httpServer.createContext("/msg",msgHandler);
        MsgImgHandler msgImgHandler = new MsgImgHandler();
        httpServer.createContext("/msg-img",msgImgHandler);
        Thread thread = new Thread(this::close);
        Runtime.getRuntime().addShutdownHook(thread);
    }

    public void start() {
        log.debug("服务器启动...");
        httpServer.start();
    }

    public void close() {
        log.debug("服务器停止...");
        httpServer.stop(5);
    }

}
