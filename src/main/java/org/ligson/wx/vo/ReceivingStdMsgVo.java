package org.ligson.wx.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivingStdMsgVo {
    private String toUserName;
    private String fromUserName;
    private long createTime;
    private String msgType;
    private String content;
    private String msgId;
}
