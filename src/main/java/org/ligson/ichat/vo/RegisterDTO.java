package org.ligson.ichat.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterDTO {

    private String username;

    private String password;

    @JsonProperty("captcha_key")
    private String captchaKey;

    @JsonProperty("captcha_code")
    private String captchaCode;
}
