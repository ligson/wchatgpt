package org.ligson.ichat.controller;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.ligson.ichat.vo.UserInfoVo;
import org.ligson.ichat.vo.WebResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ServerUserContext {
    @Value("${app.server.user-file}")
    private String userFile;
    //所有用户
    private final Map<String, UserInfoVo> userMap = new ConcurrentHashMap<>();
    //已经登录用户
    private final Map<String, UserInfoVo> onlineUserMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            File file = new File(userFile);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] users = line.split(",");
                if (users.length == 4) {
                    String username = users[0];
                    String password = users[1];
                    String level = users[2];
                    Date registerDate = DateUtils.parseDate(users[3], "yyyyMMddHHmmss");
                    UserInfoVo userInfoVo = new UserInfoVo(username, password, level, registerDate);
                    userMap.put(username, userInfoVo);
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public UserInfoVo getLoginUserByToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return onlineUserMap.get(token);
    }

    public WebResult login(String username, String password) {
        WebResult webResult = new WebResult();
        UserInfoVo vo = userMap.get(username);
        if (vo != null) {
            if (vo.getPassword().equals(password)) {
                if (vo.getLevel().equals("1")) {
                    Date endDate = DateUtils.addDays(vo.getRegisterDate(), 2);
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

    public synchronized WebResult registerUser(String username, String password, String level) {
        WebResult webResult = new WebResult();
        UserInfoVo vo = userMap.get(username);
        if (vo != null) {
            webResult.setErrorMsg("用户名已经存在");
            return webResult;
        }
        try {
            Date registerDate = Calendar.getInstance().getTime();
            String registerDateStr = DateFormatUtils.format(registerDate, "yyyyMMddHHmmss");
            PrintWriter printWriter = new PrintWriter(new FileWriter(userFile, true));
            printWriter.println(String.join(",", username, password, level, registerDateStr));
            printWriter.close();


            vo = new UserInfoVo(username, password, level, registerDate);
            userMap.put(username, vo);
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
        UserInfoVo vo = userMap.get(username);
        if (vo == null) {
            webResult.setErrorMsg("用户名不存在");
            return webResult;
        }
        if (!vo.getPassword().equals(oldPassword)) {
            webResult.setErrorMsg("旧密码不正确");
            return webResult;
        }
        try {
            File userFile1 = new File(userFile);
            FileInputStream fis = new FileInputStream(userFile1);
            String userFileContent = IOUtils.toString(fis, StandardCharsets.UTF_8);
            fis.close();
            StringBuilder builder = new StringBuilder();
            for (String user : userFileContent.split("\n")) {
                if (user.startsWith(username)) {
                    continue;
                }
                builder.append(user).append("\n");
            }

            String registerDateStr = DateFormatUtils.format(vo.getRegisterDate(), "yyyyMMddHHmmss");
            builder.append(String.join(",", username, newPassword, vo.getLevel(), registerDateStr)).append("\n");
            FileOutputStream fos = new FileOutputStream(userFile1);
            IOUtils.write(builder.toString(), fos, StandardCharsets.UTF_8);
            fos.close();
            webResult.setSuccess(true);
            vo.setPassword(newPassword);
            userMap.put(username, vo);
            return webResult;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            webResult.setErrorMsg(e.getMessage());
            return webResult;
        }
    }


    public synchronized WebResult upgrade(String username) {
        WebResult webResult = new WebResult();

        UserInfoVo vo = userMap.get(username);
        if (vo == null) {
            webResult.setErrorMsg("用户名不存在");
            return webResult;
        }

        try {
            File userFile1 = new File(userFile);
            FileInputStream fis = new FileInputStream(userFile1);
            String userFileContent = IOUtils.toString(fis, StandardCharsets.UTF_8);
            fis.close();
            StringBuilder builder = new StringBuilder();
            for (String user : userFileContent.split("\n")) {
                if (user.startsWith(username)) {
                    continue;
                }
                builder.append(user).append("\n");
            }

            String registerDateStr = DateFormatUtils.format(vo.getRegisterDate(), "yyyyMMddHHmmss");
            builder.append(String.join(",", username, vo.getPassword(), "2", registerDateStr)).append("\n");
            FileOutputStream fos = new FileOutputStream(userFile1);
            IOUtils.write(builder.toString(), fos, StandardCharsets.UTF_8);
            fos.close();
            webResult.setSuccess(true);
            vo.setLevel("2");
            userMap.put(username, vo);
            return webResult;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            webResult.setErrorMsg(e.getMessage());
            return webResult;
        }
    }


    public synchronized WebResult deleteUser(String username) {
        WebResult webResult = new WebResult();
        File users = new File(userFile);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(users));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                String[] array = line.split(",");
                if (array.length == 4) {
                    if (!username.equals(array[0])) {
                        stringBuilder.append(line).append("\n");
                    }
                }
            }
            reader.close();
            FileOutputStream fos = new FileOutputStream(users);
            IOUtils.write(stringBuilder.toString(), fos, StandardCharsets.UTF_8);
            fos.close();
            webResult.setSuccess(true);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            webResult.setSuccess(false);
            webResult.setErrorMsg("删除失败");
            webResult.setStackTrace(ExceptionUtils.getStackTrace(e));
        }
        return webResult;
    }
}
