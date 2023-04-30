package org.ligson.ichat.vo;

import lombok.Data;

@Data
public class CaptchaDTO {

    private Integer captchaKey;

    private String userInputCode;
}
