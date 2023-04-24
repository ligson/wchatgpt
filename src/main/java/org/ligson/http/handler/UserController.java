package org.ligson.http.handler;

import org.apache.commons.lang3.StringUtils;
import org.ligson.http.ServerUserContext;
import org.ligson.vo.RegisterDTO;
import org.ligson.vo.ResetPwdDTO;
import org.ligson.vo.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9]{6,12}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-z0-9_]{8,}$");
    @Value("${app.server.registerCode}")
    private String registerCode;
    @Autowired
    private ServerUserContext serverUserContext;

    @PostMapping("/resetPassword")
    public WebResult resetPassword(@RequestBody ResetPwdDTO req) {
        WebResult webResult = WebResult.newInstance();
        if (StringUtils.isNotBlank(req.getUsername()) && StringUtils.isNotBlank(req.getOldPassword()) && StringUtils.isNotBlank(req.getNewPassword())) {
            Matcher passwordMatcher = PASSWORD_PATTERN.matcher(req.getNewPassword());
            if (!passwordMatcher.matches()) {
                webResult.setErrorMsg("密码格式错误!");
                return webResult;
            }
            return serverUserContext.resetPassword(req.getUsername(), req.getOldPassword(), req.getNewPassword());
        } else {
            webResult.setErrorMsg("参数格式错误!");
            return webResult;
        }
    }

    @PostMapping("/upgrade")
    public WebResult upgrade(@RequestBody RegisterDTO req) {
        WebResult webResult = new WebResult();
        if (StringUtils.isNotBlank(req.getUsername()) && StringUtils.isNotBlank(req.getRegisterCode())) {
            if (!registerCode.equals(req.getRegisterCode())) {
                webResult.setErrorMsg("注册码错误!");
                return webResult;
            }

            Matcher usernameMatcher = USERNAME_PATTERN.matcher(req.getUsername());
            if (!usernameMatcher.matches()) {
                webResult.setErrorMsg("账号格式错误!");
                return webResult;
            }

            return serverUserContext.upgrade(req.getUsername());
        } else {
            webResult.setErrorMsg("参数格式错误!");
            return webResult;
        }
    }
}
