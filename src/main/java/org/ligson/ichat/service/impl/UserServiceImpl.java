package org.ligson.ichat.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.ligson.ichat.dao.UserDao;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.enums.UserLevel;
import org.ligson.ichat.serializer.CruxSerializer;
import org.ligson.ichat.service.UserService;
import org.ligson.ichat.vo.RegisterDTO;
import org.ligson.ichat.vo.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Lazy(value = false)
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9]{6,12}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-z0-9_]{8,}$");


    @Value("${app.server.register-code}")
    private String registerCode;

    private final static String USER_SESSION_CONTEXT_PREFIX = "xchat:session-context:";
    //已经登录用户

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CruxSerializer cruxSerializer;

    public User getLoginUserByToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        String userJson = stringRedisTemplate.boundValueOps(USER_SESSION_CONTEXT_PREFIX + ":token-user:" + token).get();
        if (StringUtils.isNotBlank(userJson)) {
            return cruxSerializer.deserialize(userJson, User.class);
        }
        return null;
    }

    public WebResult login(String username, String password) {
        WebResult webResult = new WebResult();
        User vo = userDao.findByName(username);
        if (vo != null) {
            Map<Object, Object> userTokensMap = stringRedisTemplate.boundHashOps(USER_SESSION_CONTEXT_PREFIX + ":user-tokens:" + vo.getId()).entries();
            int count = userTokensMap == null ? 0 : userTokensMap.values().size();
            if (count > 30) {
                webResult.setSuccess(false);
                webResult.setErrorMsg("最多可以使用两个设备登录，可以退出之前登录后再试");
                return webResult;
            }

            if (vo.getPassword().equals(password)) {
                if (vo.getTimes() <= 0) {
                    if (vo.getLevel() == UserLevel.FREE) {
                        webResult.setSuccess(false);
                        webResult.setErrorMsg("成本有限,免费用户只能体验20次，请联系管理员付费,请谅解");
                    } else {
                        webResult.setSuccess(false);
                        webResult.setErrorMsg("早期用户只能使用5000次，请联系管理员续费,请谅解");
                    }
                    return webResult;
                }
                vo.setLastedLoginTime(new Date());
                userDao.update(vo);
                String token = UUID.randomUUID().toString();
                webResult.setSuccess(true);
                webResult.putData("username", username);
                webResult.putData("token", token);
                stringRedisTemplate.boundHashOps(USER_SESSION_CONTEXT_PREFIX + ":user-tokens:" + vo.getId()).put(token, DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
                stringRedisTemplate.boundValueOps(USER_SESSION_CONTEXT_PREFIX + ":token-user:" + token).set(cruxSerializer.serialize(vo));
            } else {
                webResult.setSuccess(false);
                webResult.setErrorMsg("密码错误");
            }
        } else {
            webResult.setSuccess(false);
            webResult.setErrorMsg("用户不存在");
        }
        return webResult;
    }

    @Override
    public WebResult registerUser(RegisterDTO req) {
        WebResult webResult = WebResult.newInstance();
        if (StringUtils.isNotBlank(req.getUsername()) && StringUtils.isNotBlank(req.getPassword())) {

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
        } else {
            webResult.setErrorMsg("参数格式错误!");
            return webResult;
        }

        User vo = userDao.findByName(req.getUsername());
        if (vo != null) {
            webResult.setErrorMsg("用户名已经存在");
            return webResult;
        }
        try {

            User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setName(req.getUsername());
            user.setPassword(req.getPassword());
            user.setLevel(UserLevel.FREE);
            user.setTimes(5);
            userDao.insert(user);
            webResult.setSuccess(true);
            return webResult;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            webResult.setErrorMsg(e.getMessage());
            webResult.setStackTrace(ExceptionUtils.getStackTrace(e));
            return webResult;
        }
    }

    public synchronized WebResult resetPassword(String username, String oldPassword, String newPassword) {
        WebResult webResult = new WebResult();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)) {
            Matcher passwordMatcher = PASSWORD_PATTERN.matcher(newPassword);
            if (!passwordMatcher.matches()) {
                webResult.setErrorMsg("密码格式错误!");
                return webResult;
            }
            User vo = userDao.findByName(username);
            if (vo == null) {
                webResult.setErrorMsg("用户名不存在");
                return webResult;
            }
            if (!vo.getPassword().equals(oldPassword)) {
                webResult.setErrorMsg("旧密码不正确");
                return webResult;
            }
            vo.setPassword(newPassword);
            userDao.update(vo);
            webResult.setSuccess(true);
        } else {
            webResult.setErrorMsg("参数格式错误!");
        }
        return webResult;
    }


    public synchronized WebResult upgrade(String username, String regCode) {
        WebResult webResult = new WebResult();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(regCode)) {
            if (!registerCode.equals(regCode)) {
                webResult.setErrorMsg("注册码错误!");
                return webResult;
            }
            User vo = userDao.findByName(username);
            if (vo == null) {
                webResult.setErrorMsg("用户名不存在");
                return webResult;
            }
            vo.setLevel(UserLevel.FOREVER);
            vo.setTimes(5000);
            userDao.update(vo);
            webResult.setSuccess(true);
        } else {
            webResult.setErrorMsg("参数格式错误!");
        }
        return webResult;

    }


    public synchronized WebResult deleteUser(String username, String regCode) {
        WebResult webResult = new WebResult();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(regCode)) {
            if (!registerCode.equals(regCode)) {
                webResult.setErrorMsg("注册码错误!");
                return webResult;
            }
            userDao.deleteByName(username);
            webResult.setSuccess(true);
        } else {
            webResult.setErrorMsg("参数格式错误!");
        }
        return webResult;

    }

    @Override
    public WebResult logout(String token) {
        if (StringUtils.isNotBlank(token)) {
            User user = getLoginUserByToken(token);
            if (user != null) {
                stringRedisTemplate.boundHashOps(USER_SESSION_CONTEXT_PREFIX + ":user-tokens:" + user.getId()).delete(token);
                stringRedisTemplate.delete(USER_SESSION_CONTEXT_PREFIX + ":token-user:" + token);
            }
        }
        return WebResult.newSuccessInstance();
    }

    @Override
    public void fix() {
        userDao.fixUserData();
    }
}
