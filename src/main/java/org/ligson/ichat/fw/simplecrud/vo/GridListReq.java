package org.ligson.ichat.fw.simplecrud.vo;

import lombok.Data;

@Data
public class GridListReq {
    private int page = 0;
    private int max = 10;
    private String sort;
    private boolean order;
    private QueryCondition queryCondition;

}
