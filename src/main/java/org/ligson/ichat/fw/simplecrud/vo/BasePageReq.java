package org.ligson.ichat.fw.simplecrud.vo;

import lombok.Data;

@Data
public class BasePageReq {
    private int page;
    private int max;
    private String sort = "id";
    private String order = "asc";
}
