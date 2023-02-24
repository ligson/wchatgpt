package org.ligson.wx.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccessTokenRes {
    @JsonProperty("access_token")
    private String accessToken;
    //second
    @JsonProperty("expires_in")
    private long expiresIn;

    private int errcode;
    private String errmsg;

    private long createdTime;
}
