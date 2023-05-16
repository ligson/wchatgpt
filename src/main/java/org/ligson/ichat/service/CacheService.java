package org.ligson.ichat.service;

import org.ligson.ichat.domain.User;

public interface CacheService {

    /**
     * 通过token获取用户信息
     * @param token token
     * @return 用户
     */
    User getLoginUserByToken(String token);

    /**
     * 获取用户登录终端数量
     * @param userId 用户id
     * @return 终端数量
     */
    int getLoginUserTerminalCount(String userId);

    /**
     * 维护用户与token关系
     * @param token token
     * @param user 用户
     */
    void setUserAndToken(String token, User user);

    /**
     * 解除用户与token关联
     * @param user 用户
     * @param token token
     */
    void relieveUserAndToken(String token,User user);
}
