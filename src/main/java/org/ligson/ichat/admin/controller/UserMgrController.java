package org.ligson.ichat.admin.controller;

import org.ligson.ichat.admin.vo.*;
import org.ligson.ichat.user.User;
import org.ligson.ichat.user.UserService;
import org.ligson.ichat.fw.simplecrud.vo.BasePageReq;
import org.ligson.ichat.fw.simplecrud.vo.PageWebResult;
import org.ligson.ichat.fw.simplecrud.vo.WebResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/usermgr")
public class UserMgrController {
    private final UserService userService;

    public UserMgrController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/list")
    public PageWebResult<User> list(@RequestBody BasePageReq basePageReq) {
        return userService.list(basePageReq);
    }

    @PostMapping("/resetPwd")
    public WebResult resetPwd(@RequestBody ResetPwdReq req) {
        return userService.resetPwd(req);
    }

    @PostMapping("/modifyUserLevel")
    public WebResult modifyUserLevel(@RequestBody ModifyUserLevelReq req) {
        return userService.modifyUserLevel(req);
    }

    @PostMapping("/modifyUserType")
    public WebResult modifyUserType(@RequestBody ModifyUserTypeReq req) {
        return userService.modifyUserType(req);
    }

    @PostMapping("/modifyUserTimes")
    public WebResult modifyUserTimes(@RequestBody ModifyUserTimesReq req) {
        return userService.modifyUserTimes(req);
    }

    @PostMapping("/delete")
    public WebResult deleteUserReq(@RequestBody DeleteUserReq req) {
        return userService.deleteUserReq(req);
    }

    @PostMapping("/add")
    public WebResult add(@RequestBody AddUserReq addUserReq) {
        return userService.add(addUserReq);
    }


}
