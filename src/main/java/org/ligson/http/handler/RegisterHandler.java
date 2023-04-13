package org.ligson.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.ligson.constant.Constant;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.serializer.CruxSerializer;
import org.ligson.vo.LoginDTO;
import org.ligson.vo.RegisterDTO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@BootService
public class RegisterHandler implements HttpHandler {

    @BootAutowired
    private CruxSerializer cruxSerializer;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            RegisterDTO req = cruxSerializer.deserialize(IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8), RegisterDTO.class);
            if (req.getUsername() != null && req.getPassword() != null && req.getNickname() != null) {
                PrintWriter pw = new PrintWriter(new FileOutputStream(Constant.USER_FILE, true));
                pw.println(String.join(",", req.getUsername(), req.getPassword(), req.getNickname()));
                pw.close();
                Constant.LOGIN_USER_MAP.put(req.getUsername(), req.getPassword());

                byte[] buffer = "ok".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, buffer.length);
                exchange.getResponseHeaders().add("Content-Type", "application/text;charset=UTF-8");
                exchange.getResponseBody().write(buffer);
                exchange.getResponseBody().close();
                return;
            }
            log.debug("test invoke....");

            byte[] buffer = "fail".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, buffer.length);
            exchange.getResponseHeaders().add("Content-Type", "application/text;charset=UTF-8");
            exchange.getResponseBody().write(buffer);
            exchange.getResponseBody().close();
        }
    }
}
