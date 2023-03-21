package org.ligson.fw.http.server.demo;

import lombok.extern.slf4j.Slf4j;
import org.ligson.fw.http.HttpRequest;
import org.ligson.fw.http.HttpRequestParser;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Slf4j
public class NioNonBlockHttpServer {
    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(19999));
        serverSocketChannel.configureBlocking(false);
        while (true) {
            SocketChannel sc = serverSocketChannel.accept();
            if (sc == null) {
                continue;
            }
            HttpRequestParser httpRequestParser = new HttpRequestParser();
            HttpRequest httpRequest = httpRequestParser.parse(sc.socket().getInputStream());
            String msg = "<h1>xxx</h1>";
            log.debug("http:{}", httpRequest);
            StringBuilder builder = new StringBuilder();
            builder.append("HTTP/1.1 200 OK\r\n");
            builder.append("Content-Type:").append("text/html").append("\r\n");
            builder.append("Content-Length:").append(msg.getBytes().length).append("\r\n\r\n");
            builder.append(msg);
            sc.socket().getOutputStream().write(builder.toString().getBytes());
            sc.close();
        }
    }
}
