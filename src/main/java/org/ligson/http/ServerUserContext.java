package org.ligson.http;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.vo.AppConfig;
import org.ligson.vo.UserInfoVo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@BootService(initMethod = "init")
@Slf4j
public class ServerUserContext {
    @BootAutowired
    private AppConfig appConfig;
    //所有用户
    private final Map<String, UserInfoVo> userMap = new ConcurrentHashMap<>();
    //已经登录用户
    private final Map<String, UserInfoVo> onlineUserMap = new ConcurrentHashMap<>();

    public void init() {
        try {
            File file = new File(appConfig.getApp().getServer().getUserFile());
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

    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        UserInfoVo vo = userMap.get(username);
        if (vo != null) {
            if (vo.getPassword().equals(password)) {
                if (vo.getLevel().equals("1")) {
                    Date endDate = DateUtils.addDays(vo.getRegisterDate(), 2);
                    Date nowDate = Calendar.getInstance().getTime();
                    if (nowDate.getTime() < endDate.getTime()) {
                        String token = UUID.randomUUID().toString();
                        result.put("success", true);
                        result.put("token", token);
                        onlineUserMap.put(token, vo);
                    } else {
                        result.put("success", false);
                        result.put("msg", "成本有限,免费用户只能用两天，请联系管理员付费,请谅解");
                    }
                } else {
                    String token = UUID.randomUUID().toString();
                    result.put("success", true);
                    result.put("token", token);
                    onlineUserMap.put(token, vo);
                }
            } else {
                result.put("success", false);
                result.put("msg", "密码错误");
            }
        } else {
            result.put("success", false);
            result.put("msg", "用户不存在");
        }
        return result;
    }

    public synchronized Map<String, Object> registerUser(String username, String password, String level) {
        Map<String, Object> result = new HashMap<>();
        UserInfoVo vo = userMap.get(username);
        if (vo != null) {
            result.put("success", false);
            result.put("msg", "用户名已经存在");
            return result;
        }
        try {
            Date registerDate = Calendar.getInstance().getTime();
            String registerDateStr = DateFormatUtils.format(registerDate, "yyyyMMddHHmmss");
            PrintWriter printWriter = new PrintWriter(new FileWriter(appConfig.getApp().getServer().getUserFile(), true));
            printWriter.println(String.join(",", username, password, level, registerDateStr));
            printWriter.close();
            result.put("success", true);

            vo = new UserInfoVo(username, password, level, registerDate);
            userMap.put(username, vo);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.put("success", false);
            result.put("msg", e.getMessage());
            return result;
        }
    }

    public synchronized Map<String, Object> resetPassword(String username, String oldPassword, String newPassword) {
        Map<String, Object> result = new HashMap<>();
        UserInfoVo vo = userMap.get(username);
        if (vo == null) {
            result.put("success", false);
            result.put("msg", "用户名不存在");
            return result;
        }
        if (!vo.getPassword().equals(oldPassword)) {
            result.put("success", false);
            result.put("msg", "旧密码不正确");
            return result;
        }
        try {
            File userFile = new File(appConfig.getApp().getServer().getUserFile());
            FileInputStream fis = new FileInputStream(userFile);
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
            FileOutputStream fos = new FileOutputStream(userFile);
            IOUtils.write(builder.toString(), fos, StandardCharsets.UTF_8);
            fos.close();
            result.put("success", true);
            vo.setPassword(newPassword);
            userMap.put(username, vo);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.put("success", false);
            result.put("msg", e.getMessage());
            return result;
        }
    }


    public synchronized Map<String, Object> upgrade(String username) {
        Map<String, Object> result = new HashMap<>();
        UserInfoVo vo = userMap.get(username);
        if (vo == null) {
            result.put("success", false);
            result.put("msg", "用户名不存在");
            return result;
        }

        try {
            File userFile = new File(appConfig.getApp().getServer().getUserFile());
            FileInputStream fis = new FileInputStream(userFile);
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
            FileOutputStream fos = new FileOutputStream(userFile);
            IOUtils.write(builder.toString(), fos, StandardCharsets.UTF_8);
            fos.close();
            result.put("success", true);
            vo.setLevel("2");
            userMap.put(username, vo);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.put("success", false);
            result.put("msg", e.getMessage());
            return result;
        }
    }


}
