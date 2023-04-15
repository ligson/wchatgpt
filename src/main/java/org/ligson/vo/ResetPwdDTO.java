package org.ligson.vo;

import lombok.Data;

@Data
public class ResetPwdDTO {

    private String username;

    private String oldPassword;
    private String newPassword;


}
