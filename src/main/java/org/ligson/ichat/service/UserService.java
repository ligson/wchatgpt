package org.ligson.ichat.service;


import org.ligson.ichat.admin.vo.*;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.vo.BasePageReq;
import org.ligson.ichat.vo.PageWebResult;
import org.ligson.ichat.vo.RegisterDTO;
import org.ligson.ichat.vo.WebResult;

public interface UserService {
    User getLoginUserByToken(String token);

    WebResult login(String username, String password);

    WebResult registerUser(RegisterDTO registerDTO);

    WebResult modifyPassword(String username, String oldPassword, String newPassword);

    WebResult upgrade(String username, String regCode);


    WebResult deleteUser(String username, String regCode);

    WebResult logout(String token);

    void fix();

    PageWebResult<User> list(BasePageReq basePageReq);

    WebResult resetPwd(ResetPwdReq req);

    WebResult modifyUserLevel(ModifyUserLevelReq req);

    WebResult modifyUserType(ModifyUserTypeReq req);

    WebResult modifyUserTimes(ModifyUserTimesReq req);

    WebResult deleteUserReq(DeleteUserReq req);
}
