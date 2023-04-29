package org.ligson.ichat.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.ligson.ichat.dao.UserDao;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.vo.RegisterDTO;
import org.ligson.ichat.vo.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Lazy(value = false)
@Slf4j
public class UserServiceImpl implements UserService {
    @Value("${app.server.user-file}")
    private String userFile;

    @Autowired
    private UserDao userDao;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9]{6,12}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-z0-9_]{8,}$");


    @Value("${app.server.registerCode}")
    private String registerCode;


    //已经登录用户
    private final Map<String, User> onlineUserMap = new ConcurrentHashMap<>();


    public User getLoginUserByToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return onlineUserMap.get(token);
    }

    public WebResult login(String username, String password) {
        WebResult webResult = new WebResult();
        User vo = userDao.findByName(username);
        if (vo != null) {
            int count = 0;
            for (User value : onlineUserMap.values()) {
                if (value.getName().equals(vo.getName())) {
                    count++;
                }
            }
            if (count > 3) {
                webResult.setSuccess(false);
                webResult.setErrorMsg("最多可以使用两个设备登录，可以退出之前登录后再试");
                return webResult;
            }

            if (vo.getPassword().equals(password)) {
                if (vo.getLevel() == 1) {
                    Date endDate = DateUtils.addDays(vo.getCreatedTime(), 2);
                    Date nowDate = Calendar.getInstance().getTime();
                    if (nowDate.getTime() < endDate.getTime()) {
                        String token = UUID.randomUUID().toString();
                        webResult.setSuccess(true);
                        webResult.putData("username", username);
                        webResult.putData("token", token);
                        onlineUserMap.put(token, vo);
                    } else {
                        webResult.setSuccess(false);
                        webResult.putData("msg", "成本有限,免费用户只能用两天，请联系管理员付费,请谅解");
                    }
                } else {
                    String token = UUID.randomUUID().toString();
                    webResult.setSuccess(true);
                    webResult.putData("username", username);
                    webResult.putData("token", token);
                    onlineUserMap.put(token, vo);
                }
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
            user.setLevel(1);
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
            vo.setLevel(2);
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
        onlineUserMap.remove(token);
        return WebResult.newSuccessInstance();
    }
}
