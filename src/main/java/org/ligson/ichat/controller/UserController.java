package org.ligson.ichat.controller;

import org.ligson.ichat.context.SessionContext;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.service.UserService;
import org.ligson.ichat.vo.ResetPwdDTO;
import org.ligson.ichat.vo.UpgradeDTO;
import org.ligson.ichat.vo.WebResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final SessionContext sessionContext;

    public UserController(UserService userService, SessionContext sessionContext) {
        this.userService = userService;
        this.sessionContext = sessionContext;
    }

    @PostMapping("/resetPassword")
    public WebResult resetPassword(@RequestBody ResetPwdDTO req) {
        User user = sessionContext.getCurrentUser();
        return userService.resetPassword(user.getName(), req.getOldPassword(), req.getNewPassword());
    }

    @PostMapping("/upgrade")
    public WebResult upgrade(@RequestBody UpgradeDTO req) {
        return userService.upgrade(req.getUsername(), req.getRegisterCode());
    }

    @PostMapping("/delete")
    public WebResult delete(@RequestBody UpgradeDTO req) {
        return userService.deleteUser(req.getUsername(), req.getRegisterCode());
    }

    @PostMapping("/me")
    public WebResult me() {
        User user = sessionContext.getCurrentUser();
        WebResult webResult = WebResult.newSuccessInstance();
        webResult.putData("user", user);
        return webResult;
    }
}
