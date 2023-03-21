package org.ligson.fw.http;

public abstract class Servlet {
    public abstract void doService(HttpRequest httpRequest, HttpResponse httpResponse);
}
