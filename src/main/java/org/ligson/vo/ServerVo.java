package org.ligson.vo;

import lombok.Data;

@Data
public class ServerVo {
    private int port;
    private String domainUrl;

    private String registerCode;

}
