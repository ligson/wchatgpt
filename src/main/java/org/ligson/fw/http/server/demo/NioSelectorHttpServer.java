package org.ligson.fw.http.server.demo;

import lombok.extern.slf4j.Slf4j;
import org.ligson.fw.http.HttpRequest;
import org.ligson.fw.http.HttpRequestParser;
import org.ligson.fw.http.HttpResponse;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class NioSelectorHttpServer {

    private static void processRead(Selector selector, SelectionKey key) throws Exception {
        log.debug("key is read");
        SocketChannel socketChannel = (SocketChannel) key.channel();
        HttpRequestParser httpRequestParser = new HttpRequestParser(socketChannel.socket().getInputStream());
        HttpRequest httpRequest = httpRequestParser.parse();
        log.debug("http:{}", httpRequest);
        //socketChannel.configureBlocking(false);
        log.debug("isBlocking:{}", socketChannel.isBlocking());
        socketChannel.register(selector, SelectionKey.OP_WRITE);
    }

    private static void processWrite(Selector selector, SelectionKey key) throws Exception {
        log.debug("key is write");
        SocketChannel socketChannel = (SocketChannel) key.channel();
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.putHeader("Content-Type", "text/html");
        httpResponse.write(socketChannel.socket().getOutputStream(), "<h1>test</h1>".getBytes());
        socketChannel.close();
    }

    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(19999));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            int select = selector.selectNow();
            if (select == 0) {
                continue;
            }
            Set<SelectionKey> selectKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    log.debug("key is accept");
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) {
                    processRead(selector, key);
                }
                if (key.isWritable()) {
                    processWrite(selector, key);
                }

                iterator.remove();
            }
        }
    }
}
