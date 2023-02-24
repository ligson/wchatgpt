package org.ligson.wx.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CustomMsg {
    private String touser;
    private String msgtype;
    private Map<String,String> text = new HashMap<>();
    public static CustomMsg newInstance(String touser,String msgtype,String content){
        CustomMsg customMsg = new CustomMsg();
        customMsg.setTouser(touser);
        customMsg.setMsgtype(msgtype);
        customMsg.getText().put("content",content);
        return customMsg;
    }
}
