package org.ligson.ichat.dao;

import lombok.extern.slf4j.Slf4j;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.enums.UserLevel;
import org.ligson.ichat.enums.UserType;
import org.ligson.ichat.vo.BasePageReq;
import org.ligson.ichat.vo.PageWebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
@Lazy(value = false)
public class UserDao {
    //所有用户
    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void fixUserData() {

    }


    public void insert(User user) {
        jdbcTemplate.update("insert into sys_user(id,name,password,level,created_time,lasted_login_time,times,user_type) values(?,?,?,?,?,?,?,?)", ps -> {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getLevel().getCode());
            ps.setTimestamp(5, new Timestamp(new Date().getTime()));
            ps.setTimestamp(6, null);
            ps.setInt(7, user.getTimes());
            ps.setInt(8, user.getUserType().getCode());
        });
    }

    public User findByName(String name) {
        List<User> list = jdbcTemplate.query("select id,name,password,level,created_time,lasted_login_time,times,user_type from sys_user where name=?", (rs, rowNum) -> rs2User(rs), name);
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void update(User user) {
        jdbcTemplate.update("update sys_user set name=?,password=?,level=?,created_time=?,lasted_login_time=?,times=?,user_type=? where id=?", ps -> {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getLevel().getCode());
            ps.setTimestamp(4, user.getCreatedTime() != null ? new Timestamp(user.getCreatedTime().getTime()) : new Timestamp(new Date().getTime()));
            ps.setTimestamp(5, user.getLastedLoginTime() != null ? new Timestamp(user.getLastedLoginTime().getTime()) : null);
            ps.setInt(6, user.getTimes());
            ps.setInt(7, user.getUserType().getCode());
            ps.setString(8, user.getId());
        });
    }

    public void deleteByName(String username) {
        jdbcTemplate.update("delete from sys_user where name=?", username);
    }

    public synchronized void syncUserTimes() {

    }

    private User rs2User(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        user.setLevel(UserLevel.fromCode(rs.getInt("level")));
        user.setCreatedTime(rs.getDate("created_time"));
        user.setLastedLoginTime(rs.getDate("lasted_login_time"));
        user.setTimes(rs.getInt("times"));
        user.setUserType(UserType.fromCode(rs.getInt("user_type")));
        return user;
    }

    public PageWebResult<User> list(BasePageReq basePageReq) {
        List<User> list = jdbcTemplate.query("select id,name,password,level,created_time,lasted_login_time,times,user_type from sys_user order by ?,? limit ?,? ", (rs, rowNum) -> rs2User(rs), basePageReq.getSort(), basePageReq.getOrder(), (basePageReq.getPage() - 1) * basePageReq.getMax(), basePageReq.getMax());
        Integer count = jdbcTemplate.queryForObject("select count(0) from sys_user", (rs, rowNum) -> rs.getInt(1));
        return PageWebResult.newInstance(list, count == null ? 0 : count);
    }

    public void updateByIdNonFill(User user) {

    }

    public User findById(String id) {
        List<User> list = jdbcTemplate.query("select id,name,password,level,created_time,lasted_login_time,times,user_type from sys_user where id=?", (rs, rowNum) -> rs2User(rs), id);
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
