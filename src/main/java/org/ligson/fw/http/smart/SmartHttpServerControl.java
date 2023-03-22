package org.ligson.fw.http.smart;

import lombok.SneakyThrows;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;

@BootService(initMethod = "init", destoryMethod = "stop")
public class SmartHttpServerControl {
    @BootAutowired
    private SmartHttpServer smartHttpServer;

    @SneakyThrows
    public void init() {
        smartHttpServer.start();
    }

    public void stop() {
        smartHttpServer.stop();
    }

}
