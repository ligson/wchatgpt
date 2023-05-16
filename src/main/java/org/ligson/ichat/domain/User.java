package org.ligson.ichat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ligson.ichat.enums.UserLevel;
import org.ligson.ichat.enums.UserType;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;
    private String name;
    @JsonIgnore
    private String password;
    private UserLevel level;
    private Date createdTime;
    private Date lastedLoginTime;
    private Integer times;

    private UserType userType;
}
