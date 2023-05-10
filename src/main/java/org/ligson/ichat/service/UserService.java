package org.ligson.ichat.service;


import org.ligson.ichat.domain.User;
import org.ligson.ichat.vo.RegisterDTO;
import org.ligson.ichat.vo.WebResult;

public interface UserService {
    User getLoginUserByToken(String token);

    WebResult login(String username, String password);

    WebResult registerUser(RegisterDTO registerDTO);

    WebResult resetPassword(String username, String oldPassword, String newPassword);

    WebResult upgrade(String username, String regCode);


    WebResult deleteUser(String username, String regCode);

    WebResult logout(String token);

    void fix();
}
