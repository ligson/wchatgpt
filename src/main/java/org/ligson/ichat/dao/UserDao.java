package org.ligson.ichat.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.ex.InnerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@Lazy(value = false)
public class UserDao {
    //所有用户
    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    @Value("${app.server.user-file}")
    private String userFile;

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
                User user = convertString2User(line);
                if (user != null) {
                    userMap.put(user.getName(), user);
                } else {
                    log.error("转化行:{}到user失败", line);
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String convertUser2String(User user) {
        String registerDateStr = DateFormatUtils.format(user.getCreatedTime(), "yyyyMMddHHmmss");
        return String.join(",", user.getName(), user.getPassword(), user.getLevel() + "", registerDateStr);
    }

    private User convertString2User(String line) {
        String[] users = line.split(",");
        if (users.length == 4) {
            String username = users[0];
            String password = users[1];
            String level = users[2];
            Date registerDate = null;
            try {
                registerDate = DateUtils.parseDate(users[3], "yyyyMMddHHmmss");
            } catch (ParseException e) {
                throw new InnerException(e);
            }
            return new User("", username, password, Integer.parseInt(level), registerDate);
        }
        return null;
    }

    private void append(String line) {
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(new FileWriter(userFile, true));
        } catch (IOException e) {
            throw new InnerException(e);
        }
        printWriter.println(line);
        printWriter.close();
    }

    public void insert(User user) {
        user.setCreatedTime(new Date());
        String line = convertUser2String(user);
        append(line);
        userMap.put(user.getName(), user);
    }

    public User findByName(String name) {
        return userMap.get(name);
    }

    public void update(User user) {
        try {
            File userFile1 = new File(userFile);
            FileInputStream fis = new FileInputStream(userFile1);
            String userFileContent = IOUtils.toString(fis, StandardCharsets.UTF_8);
            fis.close();
            StringBuilder builder = new StringBuilder();
            for (String line : userFileContent.split("\n")) {
                if (!line.startsWith(user.getName()) || !line.split(",")[0].equals(user.getName())) {
                    builder.append(line).append("\n");
                } else {
                    String newline = convertUser2String(user);
                    builder.append(newline).append("\n");
                }
            }
            FileOutputStream fos = new FileOutputStream(userFile1);
            IOUtils.write(builder.toString(), fos, StandardCharsets.UTF_8);
            fos.close();
            userMap.put(user.getName(), user);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InnerException(e);
        }
    }

    public static void main(String[] args) {

    }

    public void deleteByName(String username) {
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
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new InnerException(e);
        }
    }
}
