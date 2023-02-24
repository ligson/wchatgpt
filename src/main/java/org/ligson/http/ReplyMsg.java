package org.ligson.http;

import lombok.Data;

@Data
public class ReplyMsg {
    private String msg;
    private boolean timeout;
}
