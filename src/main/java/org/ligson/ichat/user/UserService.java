package org.ligson.ichat.user;


import org.ligson.ichat.admin.vo.*;
import org.ligson.ichat.fw.simplecrud.service.CrudService;
import org.ligson.ichat.fw.simplecrud.vo.BasePageReq;
import org.ligson.ichat.fw.simplecrud.vo.PageWebResult;
import org.ligson.ichat.fw.simplecrud.vo.WebResult;
import org.ligson.ichat.vo.RegisterDTO;

public interface UserService extends CrudService<User> {
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

    WebResult add(AddUserReq addUserReq);
}
