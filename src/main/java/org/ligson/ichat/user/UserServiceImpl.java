package org.ligson.ichat.user;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.ligson.ichat.admin.vo.*;
import org.ligson.ichat.enums.UserLevel;
import org.ligson.ichat.enums.UserType;
import org.ligson.ichat.fw.ex.BussinessException;
import org.ligson.ichat.fw.simplecrud.vo.BasePageReq;
import org.ligson.ichat.fw.simplecrud.vo.PageWebResult;
import org.ligson.ichat.fw.simplecrud.vo.WebResult;
import org.ligson.ichat.service.CacheService;
import org.ligson.ichat.vo.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    @Autowired
    private CacheService cacheService;

    public User getLoginUserByToken(String token) {
        return cacheService.getLoginUserByToken(token);
    }

    public WebResult login(String username, String password) {
        WebResult webResult = new WebResult();
        User user = userDao.findOne(QUser.user.name.eq(username)).orElseThrow(() -> new BussinessException(String.format("用户名%s不存在", username)));
        if (user != null) {
            int count = cacheService.getLoginUserTerminalCount(user.getId());
            if (count > 30) {
                //webResult.setSuccess(false);
                //webResult.setErrorMsg("最多可以使用两个设备登录，可以退出之前登录后再试");
                //return webResult;
                log.warn("最多可以使用两个设备登录，可以退出之前登录后再试");
            }

            if (user.getPassword().equals(password)) {
                if (user.getTimes() <= 0) {
                    if (user.getLevel() == UserLevel.FREE) {
                        webResult.setSuccess(false);
                        webResult.setErrorMsg("成本有限,免费用户只能体验20次，请联系管理员付费,请谅解");
                    } else {
                        webResult.setSuccess(false);
                        webResult.setErrorMsg("早期用户只能使用5000次，请联系管理员续费,请谅解");
                    }
                    return webResult;
                }
                user.setLastedLoginTime(new Date());
                update(user);
                String token = UUID.randomUUID().toString();
                webResult.setSuccess(true);
                webResult.putData("username", username);
                webResult.putData("token", token);
                cacheService.setUserAndToken(token, user);
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

        long count = userDao.count(QUser.user.name.eq(req.getUsername()));
        if (count > 0) {
            webResult.setErrorMsg("用户名已经存在");
            return webResult;
        }
        try {

            User user = new User();
            user.setName(req.getUsername());
            user.setPassword(req.getPassword());
            user.setLevel(UserLevel.FREE);
            user.setTimes(5);
            user.setUserType(UserType.NORMAL);
            save(user);
            webResult.setSuccess(true);
            return webResult;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            webResult.setErrorMsg(e.getMessage());
            webResult.setStackTrace(ExceptionUtils.getStackTrace(e));
            return webResult;
        }
    }

    public synchronized WebResult modifyPassword(String username, String oldPassword, String newPassword) {
        WebResult webResult = new WebResult();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)) {
            Matcher passwordMatcher = PASSWORD_PATTERN.matcher(newPassword);
            if (!passwordMatcher.matches()) {
                webResult.setErrorMsg("密码格式错误!");
                return webResult;
            }
            User vo = userDao.findOne(QUser.user.name.eq(username)).orElseThrow(() -> new BussinessException(String.format("用户名%s不存在", username)));
            if (!vo.getPassword().equals(oldPassword)) {
                webResult.setErrorMsg("旧密码不正确");
                return webResult;
            }
            vo.setPassword(newPassword);
            update(vo);
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
            User vo = userDao.findOne(QUser.user.name.eq(username)).orElseThrow(() -> new BussinessException(String.format("用户名%s不存在", username)));
            vo.setLevel(UserLevel.FOREVER);
            vo.setTimes(5000);
            update(vo);
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
            User user = userDao.findOne(QUser.user.name.eq(username)).orElseThrow(() -> new BussinessException(String.format("用户名%s不存在", username)));
            delete(user);
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
                cacheService.relieveUserAndToken(token, user);
            }
        }
        return WebResult.newSuccessInstance();
    }

    @Override
    public void fix() {
        // userDao.fixUserData();
    }

    @Override
    public PageWebResult<User> list(BasePageReq basePageReq) {
        Page<User> page = userDao.findAll(PageRequest.of(basePageReq.getPage(), basePageReq.getMax(), Sort.by(Sort.Direction.valueOf(basePageReq.getOrder()), basePageReq.getSort())));
        return PageWebResult.newInstance(page.getContent(), page.getTotalElements());
    }

    @Override
    public WebResult resetPwd(ResetPwdReq req) {
        WebResult webResult = new WebResult();
        Matcher passwordMatcher = PASSWORD_PATTERN.matcher(req.getPassword());
        if (!passwordMatcher.matches()) {
            webResult.setErrorMsg("密码格式错误!");
            return webResult;
        }
        User user = findById(req.getId(), User.class);
        user.setPassword(req.getPassword());
        update(user);
        webResult.setSuccess(true);
        return webResult;
    }

    @Override
    public WebResult modifyUserLevel(ModifyUserLevelReq req) {
        User user = findById(req.getId(), User.class);
        user.setLevel(req.getLevel());
        update(user);
        return WebResult.newSuccessInstance();
    }

    @Override
    public WebResult modifyUserType(ModifyUserTypeReq req) {
        User user = findById(req.getId(), User.class);
        user.setUserType(req.getUserType());
        update(user);
        return WebResult.newSuccessInstance();
    }

    @Override
    public WebResult modifyUserTimes(ModifyUserTimesReq req) {
        User user = findById(req.getId(), User.class);
        user.setTimes(req.getTimes());
        update(user);
        return WebResult.newSuccessInstance();
    }

    @Override
    public WebResult deleteUserReq(DeleteUserReq req) {
        User user = findById(req.getId(), User.class);
        delete(user);
        return WebResult.newSuccessInstance();
    }

    @Override
    public WebResult add(AddUserReq req) {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername(req.getName());
        registerDTO.setPassword(req.getPassword());
        return registerUser(registerDTO);
    }
}
