package org.ligson.ichat.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVo {
    private String username;
    private String password;
    private String level;
    private Date registerDate;
}
