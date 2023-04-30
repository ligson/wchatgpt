package org.ligson.ichat.controller;

import org.ligson.ichat.service.UserService;
import org.ligson.ichat.vo.RegisterDTO;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/resetPassword")
    public WebResult resetPassword(@RequestBody ResetPwdDTO req) {
        return userService.resetPassword(req.getUsername(), req.getOldPassword(), req.getNewPassword());
    }

    @PostMapping("/upgrade")
    public WebResult upgrade(@RequestBody UpgradeDTO req) {
        return userService.upgrade(req.getUsername(), req.getRegisterCode());
    }

    @PostMapping("/delete")
    public WebResult delete(@RequestBody UpgradeDTO req) {
        return userService.deleteUser(req.getUsername(), req.getRegisterCode());
    }
}
