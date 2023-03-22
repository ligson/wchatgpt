package org.ligson.smarthttp.demo;

import lombok.extern.slf4j.Slf4j;
import org.ligson.fw.annotation.BootService;
import org.ligson.fw.http.HttpRequest;
import org.ligson.fw.http.HttpResponse;
import org.ligson.fw.http.smart.SmartServlet;

import java.io.IOException;

@BootService
@Slf4j
public class HelloSmartServlet extends SmartServlet {

    @Override
    public boolean match(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        return "/hello".equals(path);
    }

    @Override
    public void doService(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        log.debug(".....{}", httpRequest.getBody());
        httpResponse.write("hello".getBytes());
        httpResponse.write("hello".getBytes());
    }
}
