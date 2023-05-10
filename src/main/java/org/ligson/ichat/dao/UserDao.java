package org.ligson.ichat.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.ex.InnerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
@Lazy(value = false)
public class UserDao {
    //所有用户
    @Value("${app.server.user-file}")
    private String userFile;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public void fixUserData() {
        List<User> users = new ArrayList<>();
        try {
            File userFile1 = new File(userFile);
            FileInputStream fis = new FileInputStream(userFile1);
            String userFileContent = IOUtils.toString(fis, StandardCharsets.UTF_8);
            fis.close();
            StringBuilder builder = new StringBuilder();
            for (String line : userFileContent.split("\n")) {
                User user = convertString2User(line);
                if (user != null) {
                    builder.append(convertUser2String(user)).append("\n");
                    users.add(user);
                }
            }
            FileOutputStream fos = new FileOutputStream(userFile1);
            IOUtils.write(builder.toString(), fos, StandardCharsets.UTF_8);
            fos.close();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InnerException(e);
        }
        for (User user : users) {
            try {
                insert(user);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }

    }

    private String convertUser2String(User user) {
        String registerDateStr = DateFormatUtils.format(user.getCreatedTime(), "yyyyMMddHHmmss");
        return String.join(",", user.getName(), user.getPassword(), user.getLevel() + "", registerDateStr, user.getTimes() + "");
    }

    private User convertString2User(String line) {
        String[] users = line.split(",");

        if (users.length == 4) {
            String[] users2 = new String[5];
            users2[0] = users[0];
            users2[1] = users[1];
            users2[2] = users[2];
            users2[3] = users[3];
            users2[4] = users[2].equals("1") ? "20" : "5000";
            users = users2;
        }
        if (users.length == 5) {
            String username = users[0];
            String password = users[1];
            String level = users[2];
            String times = users[4].trim();
            Date registerDate;
            try {
                registerDate = DateUtils.parseDate(users[3], "yyyyMMddHHmmss");
            } catch (ParseException e) {
                throw new InnerException(e);
            }
            return new User("", username, password, Integer.parseInt(level), registerDate, null, Integer.parseInt(times));
        }
        return null;
    }

    public void insert(User user) {
        jdbcTemplate.update("insert into sys_user(id,name,password,level,created_time,lasted_login_time,times) values(?,?,?,?,?,?,?)", ps -> {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getLevel());
            ps.setTimestamp(5, new Timestamp(new Date().getTime()));
            ps.setTimestamp(6, null);
            ps.setInt(7, user.getTimes());
        });
    }

    public User findByName(String name) {
        List<User> list = jdbcTemplate.query("select id,name,password,level,created_time,lasted_login_time,times from sys_user where name=?", (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setLevel(rs.getInt("level"));
            user.setCreatedTime(rs.getDate("created_time"));
            user.setLastedLoginTime(rs.getDate("lasted_login_time"));
            user.setTimes(rs.getInt("times"));
            return user;
        }, name);
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void update(User user) {
        jdbcTemplate.update("update sys_user set name=?,password=?,level=?,created_time=?,lasted_login_time=?,times=? where id=?", ps -> {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getLevel());
            ps.setTimestamp(4, new Timestamp(new Date().getTime()));
            ps.setTimestamp(5, user.getLastedLoginTime() != null ? new Timestamp(user.getLastedLoginTime().getTime()) : null);
            ps.setInt(6, user.getTimes());
            ps.setString(7, user.getId());
        });
    }

    public void deleteByName(String username) {
        jdbcTemplate.update("delete from sys_user where name=?", username);
    }

    public synchronized void syncUserTimes() {

    }
}
