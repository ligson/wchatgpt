package org.ligson.ichat.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.ligson.ichat.enums.UserLevel;
import org.ligson.ichat.enums.UserType;
import org.ligson.ichat.fw.simplecrud.domain.BaseEntity;

import java.util.Date;

@Table(name = "t_user", uniqueConstraints = {@UniqueConstraint(name = "uk_user_name", columnNames = "name")})
@Comment("用户表")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(nullable = false,unique = true)
    @Comment("名称")
    private String name;
    @Column(nullable = false)
    @Comment("密码")
    @JsonIgnore
    private String password;
    @Column(nullable = false)
    @Comment("用户级别:1、免费;2、收费")
    @Enumerated(value = EnumType.STRING)
    private UserLevel level;
    @Column
    @Comment("上次登录时间")
    private Date lastedLoginTime;
    @Column(nullable = false)
    @Comment("接口剩余次数")
    private Integer times;

    @Column(nullable = false)
    @Comment("用户类型:1.普通用户,100.管理员")
    @Enumerated(value = EnumType.STRING)
    private UserType userType;
}
