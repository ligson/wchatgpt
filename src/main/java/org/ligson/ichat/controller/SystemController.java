package org.ligson.ichat.controller;

import org.ligson.ichat.vo.WebResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sys")
public class SystemController {
    @Value("${app.customer.wx.id}")
    private String customerId;
    @Value("${app.customer.wx.qr_code}")
    private String customerQrCode;

    @GetMapping("/customerInfo")
    public WebResult customer() {
        WebResult webResult = WebResult.newSuccessInstance();
        webResult.putData("wxId", customerId);
        webResult.putData("wxQrCode", customerQrCode);
        return webResult;
    }
}
