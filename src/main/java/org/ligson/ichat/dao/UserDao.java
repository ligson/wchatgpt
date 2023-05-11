package org.ligson.ichat.dao;

import lombok.extern.slf4j.Slf4j;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.enums.UserLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
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

    }


    public void insert(User user) {
        jdbcTemplate.update("insert into sys_user(id,name,password,level,created_time,lasted_login_time,times) values(?,?,?,?,?,?,?)", ps -> {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getLevel().getCode());
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
            user.setLevel(UserLevel.fromCode(rs.getInt("level")));
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
            ps.setInt(3, user.getLevel().getCode());
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
