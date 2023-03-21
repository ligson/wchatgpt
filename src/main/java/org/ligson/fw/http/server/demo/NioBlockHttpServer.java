package org.ligson.fw.http.server.demo;

import lombok.extern.slf4j.Slf4j;
import org.ligson.fw.http.HttpRequest;
import org.ligson.fw.http.HttpRequestParser;
import org.ligson.fw.http.HttpResponse;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


@Slf4j
public class NioBlockHttpServer {
    public static void main(String[] args) throws Exception {


        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(19999));


        while (true) {
            SocketChannel client = serverSocketChannel.accept();
            HttpRequestParser httpRequestParser = new HttpRequestParser(client.socket().getInputStream());
            HttpRequest httpRequest = httpRequestParser.parse();
            log.debug("http request:{}", httpRequest);
            StringBuilder builder = new StringBuilder();
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.putHeader("Content-Type", "text/html");
            httpResponse.write(client.socket().getOutputStream(), "<h1>test</h1>".getBytes());
            client.close();
        }

    }
}
