package org.ligson.ichat.vo;

import lombok.Data;

@Data
public class ServerVo {
    private int port;
    private String domainUrl;

    private String registerCode;

    private String userFile;

}
