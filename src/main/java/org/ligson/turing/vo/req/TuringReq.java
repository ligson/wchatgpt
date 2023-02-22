package org.ligson.turing;

import lombok.Data;

@Data
public class TuringReq {
    //输入类型:0-文本(默认)、1-图片、2-音频
    private int reqType = 0;
    private Perception perception;
    private UserInfo userInfo;

    public static TuringReq create(String userId, String apiKey, String text) {
        TuringReq turingReq = new TuringReq();
        Perception perception1 = new Perception();
        InputText text1 = new InputText();
        text1.setText(text);
        perception1.setInputText(text1);
        turingReq.setPerception(perception1);
        UserInfo userInfo1 = new UserInfo();
        userInfo1.setUserId(userId);
        userInfo1.setApiKey(apiKey);
        turingReq.setUserInfo(userInfo1);
        return turingReq;
    }
}
