package org.ligson.ichat.vo;

import lombok.Data;

@Data
public class WXVo {
    private String token;
    private String appId;
    private String appSecret;
    private String msgPath;
}
