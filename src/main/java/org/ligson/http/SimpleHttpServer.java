package org.ligson.http;

import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.http.handler.MsgHandler;
import org.ligson.http.handler.MsgImgHandler;
import org.ligson.http.handler.TestHandler;
import org.ligson.http.handler.WxHandler;
import org.ligson.vo.AppConfig;

import java.net.InetSocketAddress;

@BootService(initMethod = "init")
@Slf4j
public class SimpleHttpServer {

    private HttpServer httpServer;
    @BootAutowired
    private AppConfig appConfig;
    @BootAutowired
    private WxHandler wxHandler;
    @BootAutowired
    private MsgHandler msgHandler;
    @BootAutowired
    private MsgImgHandler msgImgHandler;

    @BootAutowired
    private TestHandler testHandler;

    public void init() throws Exception {
        httpServer = HttpServer.create(new InetSocketAddress(appConfig.getApp().getServer().getPort()), 100);

        httpServer.createContext("/test", testHandler);
        httpServer.createContext("/auth", wxHandler);
        httpServer.createContext("/msg", msgHandler);
        httpServer.createContext("/msg-img", msgImgHandler);
        Thread thread = new Thread(this::close);
        Runtime.getRuntime().addShutdownHook(thread);

        start();
    }

    public void start() {
        log.debug("服务器启动，端口:{}...", appConfig.getApp().getServer().getPort());
        httpServer.start();
    }

    public void close() {
        log.debug("服务器停止...");
        httpServer.stop(5);
    }

}
