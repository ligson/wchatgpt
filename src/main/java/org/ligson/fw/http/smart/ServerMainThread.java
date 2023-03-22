package org.ligson.fw.http.smart;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.ligson.fw.http.HttpRequest;
import org.ligson.fw.http.HttpRequestParser;
import org.ligson.fw.http.HttpResponse;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Slf4j
public class ServerMainThread implements Runnable {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private List<SmartServlet> smartServletList;

    public ServerMainThread(Selector selector,
                            ServerSocketChannel serverSocketChannel,
                            List<SmartServlet> smartServletList) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
        this.smartServletList = smartServletList;
    }

    private void processAccept(Selector selector, SelectionKey key) throws IOException {
        log.debug("key is accept:{}", key.hashCode());
        SocketChannel socketChannel = serverSocketChannel.accept();
        //SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void processRead(Selector selector, SelectionKey key) throws IOException {
        log.debug("key is read:{}", key.hashCode());
        SocketChannel socketChannel = (SocketChannel) key.channel();
        System.out.println("--" + socketChannel.isBlocking());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        HttpRequest httpRequest = httpRequestParser.parse(socketChannel);
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setSelectionKey(key);
        for (SmartServlet smartServlet : smartServletList) {
            if (smartServlet.match(httpRequest)) {
                smartServlet.doService(httpRequest, httpResponse);
                break;
            }
        }

        key.attach(httpResponse);
        //log.debug("http:{}", httpRequest);
        //socketChannel.configureBlocking(false);
        //log.debug("isBlocking:{}", socketChannel.isBlocking());
        //socketChannel.register(selector, SelectionKey.OP_WRITE);
    }

    private void processWrite(Selector selector, SelectionKey key) throws IOException {
        log.debug("key is write:{}", key.hashCode());
        SocketChannel socketChannel = (SocketChannel) key.channel();
        //HttpResponse httpResponse = new HttpResponse();
        //httpResponse.putHeader("Content-Type", "text/html");
        //httpResponse.write(socketChannel, "<h1>test</h1>".getBytes());
        //socketChannel.close();
        HttpResponse httpResponse = (HttpResponse) key.attachment();
        if (httpResponse != null) {
            httpResponse.flush();
            socketChannel.close();
        }
    }

    private void processSelector(Set<SelectionKey> selectKeys, Selector selector) {
        Iterator<SelectionKey> iterator = selectKeys.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            if (key.isAcceptable()) {
                try {
                    processAccept(selector, key);
                } catch (IOException e) {
                    log.warn("accept exception:{},stack:{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
                    continue;
                }
            }
            if (key.isReadable()) {
                try {
                    processRead(selector, key);
                } catch (IOException e) {
                    log.warn("read exception:{},stack:{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
                    continue;
                }

            }
            if (key.isWritable()) {
                try {
                    processWrite(selector, key);
                } catch (IOException e) {
                    log.warn("write exception:{},stack:{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
                    continue;
                }

            }
            iterator.remove();
        }
    }

    @Override
    public void run() {
        while (true) {
            int select;
            try {
                select = selector.select(100);
            } catch (IOException e) {
                log.warn("select exception:{}", e.getMessage());
                continue;
            }
            if (select == 0) {
                continue;
            }
            Set<SelectionKey> selectKeys = selector.selectedKeys();
            processSelector(selectKeys, selector);
        }
    }
}
