package org.ligson.ichat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;
    private String name;
    private String password;
    private Integer level;
    private Date createdTime;
}
