package org.ligson.smarthttp.demo;

import lombok.extern.slf4j.Slf4j;
import org.ligson.fw.http.HttpRequest;
import org.ligson.fw.http.enums.HttpMethod;
import org.ligson.fw.web.annotation.BootController;
import org.ligson.fw.web.annotation.BootRequestBody;
import org.ligson.fw.web.annotation.BootRequestMapping;
import org.ligson.fw.web.annotation.BootRequestParam;
import org.ligson.smarthttp.demo.vo.LoginReq;
import org.ligson.smarthttp.demo.vo.LoginRes;

@Slf4j
@BootController
@BootRequestMapping("/demo")
public class DemoController {
    @BootRequestMapping("/get_test")
    public String get_test(@BootRequestParam String name) {
        log.debug("name:{}", name);
        return "ok";
    }

    @BootRequestMapping(value = "/post_test", method = HttpMethod.POST)
    public LoginRes post_test(@BootRequestBody LoginReq loginReq) {
        log.debug("loginReq:{}", loginReq);
        return new LoginRes();
    }

    @BootRequestMapping(value = "/upload", method = HttpMethod.POST)
    public LoginRes upload(HttpRequest httpRequest) {
        log.debug("loginReq:{}", httpRequest);
        return new LoginRes();
    }
}
