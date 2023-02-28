package org.ligson.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.ligson.vo.AppConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class MsgImgHandler implements HttpHandler {
    private AppConfig appConfig;

    public MsgImgHandler() {
        try {
            appConfig = AppConfig.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        log.debug("请求path:{}", path);
        //"/msg-img/user/msg/img"
        String[] arr = path.split("/");
        if (arr.length != 5) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            return;
        }
        String user = arr[2];
        String msg = arr[3];
        String img = arr[4];
        String imgDir = appConfig.getApp().getWx().getMsgPath() + "/" + user + "/" + msg;
        File file = new File(imgDir, img);
        String mimeType = Files.probeContentType(file.toPath());
        exchange.getResponseHeaders().add("Content-Type", mimeType);
        exchange.sendResponseHeaders(200, file.length());
        FileInputStream fis = new FileInputStream(file);
        IOUtils.copy(fis, exchange.getResponseBody());
        fis.close();
        exchange.close();
    }
}
