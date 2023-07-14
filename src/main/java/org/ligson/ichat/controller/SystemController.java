package org.ligson.ichat.controller;

import org.ligson.ichat.user.UserService;
import org.ligson.ichat.vo.RegisterDTO;
import org.ligson.ichat.vo.UpgradeDTO;
import org.ligson.ichat.fw.simplecrud.vo.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sys")
public class SystemController {
    @Value("${app.customer.wx.id}")
    private String customerId;
    @Value("${app.customer.wx.qr_code}")
    private String customerQrCode;

    @Autowired
    private UserService userService;

    @GetMapping("/customerInfo")
    public WebResult customer() {
        WebResult webResult = WebResult.newSuccessInstance();
        webResult.putData("wxId", customerId);
        webResult.putData("wxQrCode", customerQrCode);
        return webResult;
    }

    @PostMapping("/register")
    public WebResult register(@RequestBody RegisterDTO req) {
        return userService.registerUser(req);
    }

    @PostMapping("/upgrade")
    public WebResult upgrade(@RequestBody UpgradeDTO req) {
        return userService.upgrade(req.getUsername(), req.getRegisterCode());
    }
}
