package org.ligson.fw.http.smart;

import org.ligson.fw.http.HttpRequest;
import org.ligson.fw.http.HttpResponse;

import java.io.IOException;

public abstract class SmartServlet {
    public abstract boolean match(HttpRequest httpRequest);

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
    }

    public void doService(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String method = httpRequest.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            doGet(httpRequest, httpResponse);
        } else if (("POST".equalsIgnoreCase(method))) {
            doPost(httpRequest, httpResponse);
        }
    }
}
