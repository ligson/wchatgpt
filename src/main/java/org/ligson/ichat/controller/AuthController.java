package org.ligson.ichat.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.service.UserService;
import org.ligson.ichat.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public WebResult login(@RequestBody LoginDTO req) throws IOException {
        return userService.login(req.getUsername(), req.getPassword());
    }

    @PostMapping("/logout")
    public WebResult logout(@RequestBody TokenDTO tokenDTO) {
        return userService.logout(tokenDTO.getToken());
    }

    @PostMapping("/checkLogin")
    public WebResult checkLogin(@RequestBody TokenDTO tokenDTO) {
        WebResult webResult = WebResult.newInstance();
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
        return userService.registerUser(req);
    }
}
