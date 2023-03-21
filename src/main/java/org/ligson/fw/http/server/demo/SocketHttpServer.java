package org.ligson.fw.http.server.demo;

import lombok.extern.slf4j.Slf4j;
import org.ligson.fw.http.HttpRequest;
import org.ligson.fw.http.HttpRequestParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


@Slf4j
public class SocketHttpServer {
    public static void main(String[] args) throws Exception {


        ServerSocket serverSocket = new ServerSocket(19999);

        while (true) {
            Socket client = serverSocket.accept();
            OutputStream oos = client.getOutputStream();
            HttpRequestParser httpRequestParser = new HttpRequestParser();
            HttpRequest req = httpRequestParser.parse(client.getInputStream());
            log.debug("http request:{}", req);
            String msg = "<h1>test</h1>";
            byte[] msgBuffer = msg.getBytes();

            StringBuilder builder = new StringBuilder();
            builder.append("HTTP/1.0 200 OK\r\n");
            builder.append("Content-Length: ").append(msgBuffer.length).append("\r\n");
            builder.append("Content-Type: text/html; charset=utf-8").append("\r\n");
            builder.append(msg);
            builder.append("\r\n");
            //BufferedOutputStream oos = new BufferedOutputStream(client.getOutputStream());
            //oos.write("HTTP/1.1 405 Method Not Allowed\r\n\r\n".getBytes("UTF-8"));
            //oos.write(builder.toString().getBytes());
            //oos.flush();

            String response = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\n\r\n<html><body><h1>Hello, world!</h1></body></html>";
            oos.write(response.getBytes(StandardCharsets.UTF_8));
            oos.flush();
            oos.close();
        }

    }
}
