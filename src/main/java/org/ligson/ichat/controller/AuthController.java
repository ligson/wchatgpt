package org.ligson.ichat.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9]{6,12}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-z0-9_]{8,}$");


    @Value("${app.server.registerCode}")
    private String registerCode;
    @Autowired
    private ServerUserContext serverUserContext;

    @PostMapping("/login")
    public WebResult login(@RequestBody LoginDTO req) throws IOException {
        return serverUserContext.login(req.getUsername(), req.getPassword());
    }

    @PostMapping("/checkLogin")
    public WebResult checkLogin(@RequestBody TokenDTO tokenDTO) {
        WebResult webResult = WebResult.newInstance();
        UserInfoVo userInfo = serverUserContext.getLoginUserByToken(tokenDTO.getToken());
        if (userInfo != null) {
            webResult.setSuccess(true);
            webResult.putData("username", userInfo.getUsername());
            webResult.putData("token", tokenDTO.getToken());
        } else {
            webResult.setSuccess(false);
            webResult.setErrorMsg("用户已经过期,请重新登录");
        }
        return webResult;
    }

    @PostMapping("/register")
    public WebResult register(@RequestBody RegisterDTO req) {
        WebResult webResult = WebResult.newInstance();
        if (StringUtils.isNotBlank(req.getUsername()) && StringUtils.isNotBlank(req.getPassword()) && StringUtils.isNotBlank(req.getRegisterCode())) {
            if (!registerCode.equals(req.getRegisterCode())) {
                webResult.setErrorMsg("注册码错误!");
                return webResult;
            }

            Matcher usernameMatcher = USERNAME_PATTERN.matcher(req.getUsername());
            if (!usernameMatcher.matches()) {
                webResult.setErrorMsg("账号格式错误!");
                return webResult;
            }

            Matcher passwordMatcher = PASSWORD_PATTERN.matcher(req.getPassword());
            if (!passwordMatcher.matches()) {
                webResult.setErrorMsg("密码格式错误!");
                return webResult;
            }
            return serverUserContext.registerUser(req.getUsername(), req.getPassword(), "1");
        } else {
            webResult.setErrorMsg("参数格式错误!");
            return webResult;
        }
    }
}
