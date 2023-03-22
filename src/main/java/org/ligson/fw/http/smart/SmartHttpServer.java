package org.ligson.fw.http.smart;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SmartHttpServer {
    private ServerSocketChannel serverSocketChannel;
    private Thread serverThread;
    private ServerMainThread serverMainThread;

    private final List<SmartServlet> smartServletList = new ArrayList<>();


    public static SmartHttpServer create(InetSocketAddress inetSocketAddress) throws IOException {
        SmartHttpServer smartHttpServer = new SmartHttpServer();
        smartHttpServer.serverSocketChannel = ServerSocketChannel.open();
        smartHttpServer.serverSocketChannel.socket().bind(inetSocketAddress);
        return smartHttpServer;
    }

    public void start() throws IOException {
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        serverMainThread = new ServerMainThread(selector, serverSocketChannel,smartServletList);
        serverThread = new Thread(serverMainThread);
        serverThread.start();
    }

    public void stop() {
        serverThread.interrupt();
    }

    public void bindSmartServlet(SmartServlet smartServlet) {
        smartServletList.add(smartServlet);
    }
}
