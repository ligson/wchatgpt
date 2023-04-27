package org.ligson.ichat.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterDTO {

    private String username;

    private String password;

    @JsonProperty("register_code")
    private String registerCode;
}
