package org.ligson.ichat.controller;

import lombok.extern.slf4j.Slf4j;
import org.ligson.ichat.fw.simplecrud.vo.WebResult;
import org.ligson.ichat.user.User;
import org.ligson.ichat.fw.ex.InnerException;
import org.ligson.ichat.service.CaptchaService;
import org.ligson.ichat.user.UserService;
import org.ligson.ichat.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/login")
    public WebResult login(@RequestBody LoginDTO req) throws IOException {
        WebResult captchaRes = captchaService.verify(Integer.valueOf(req.getCaptchaKey()), req.getCaptchaCode());
        if (!captchaRes.isSuccess()) {
            return captchaRes;
        }
        return userService.login(req.getUsername(), req.getPassword());
    }

    @PostMapping("/logout")
    public WebResult logout(@RequestBody TokenDTO tokenDTO) {
        return userService.logout(tokenDTO.getToken());
    }

    @PostMapping("/checkLogin")
    public WebResult checkLogin(@RequestBody TokenDTO tokenDTO) {
        WebResult webResult = WebResult.newInstance();
        if(tokenDTO.getToken()==null){
            throw new InnerException("ss");
        }

        User userInfo = userService.getLoginUserByToken(tokenDTO.getToken());
        if (userInfo != null) {
            webResult.setSuccess(true);
            webResult.putData("username", userInfo.getName());
            webResult.putData("token", tokenDTO.getToken());
        } else {
            webResult.setSuccess(false);
            webResult.setErrorMsg("用户已经过期,请重新登录");
        }
        return webResult;
    }

    @PostMapping("/register")
    public WebResult register(@RequestBody RegisterDTO req) {
        WebResult captchaRes = captchaService.verify(Integer.valueOf(req.getCaptchaKey()), req.getCaptchaCode());
        if (!captchaRes.isSuccess()) {
            return captchaRes;
        }
        return userService.registerUser(req);
    }

    @GetMapping("/getCaptcha")
    public WebResult getCaptcha() {
        return captchaService.generate();
    }

    @PostMapping("/verifyCaptcha")
    public WebResult verify(@RequestBody CaptchaDTO req) {
        return captchaService.verify(req.getCaptchaKey(), req.getUserInputCode());
    }

}
