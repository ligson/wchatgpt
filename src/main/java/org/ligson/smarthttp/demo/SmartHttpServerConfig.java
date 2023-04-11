package org.ligson.smarthttp.demo;

import org.ligson.fw.http.smart.SmartHttpServer;
import org.ligson.fw.http.smart.SmartServlet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

//@BootConfig
public class SmartHttpServerConfig {
    //@BootBean
    public SmartHttpServer smartHttpServer(List<SmartServlet> smartServlets) throws IOException {
        SmartHttpServer server = SmartHttpServer.create(new InetSocketAddress(19999));
        for (SmartServlet smartServlet : smartServlets) {
            server.bindSmartServlet(smartServlet);
        }
        return server;
    }
}
