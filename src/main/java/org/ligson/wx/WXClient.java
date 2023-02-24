package org.ligson.wx;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.ligson.serializer.CruxSerializer;
import org.ligson.serializer.jackson.JacksonSerializer;
import org.ligson.util.MyHttpClient;
import org.ligson.vo.AppConfig;
import org.ligson.vo.WXVo;
import org.ligson.wx.vo.AccessTokenRes;
import org.ligson.wx.vo.CustomMsg;
import org.ligson.wx.vo.CustomMsgRes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
public class WXClient {
    private static final String baseUrl = "https://api.weixin.qq.com/cgi-bin";
    private MyHttpClient myHttpClient = new MyHttpClient();
    AppConfig appConfig;
    private static AccessTokenRes accessTokenRes;
    private CruxSerializer cruxSerializer;

    public WXClient() {
        try {
            appConfig = AppConfig.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cruxSerializer = new JacksonSerializer();
    }

    public void pushMsg(String toUser, String msg) {
        CustomMsg cm = CustomMsg.newInstance(toUser, "text", msg);
        try {
            String accessToken = getAccessToken();
            CustomMsgRes result = myHttpClient.doPost(baseUrl + "/message/custom/send?access_token=" + accessToken, Collections.emptyList(), cm, CustomMsgRes.class);
            log.debug("推送客服消息返回:{}", result);
            //{"errcode":48001,"errmsg":"api unauthorized rid: 63f86224-447b3844-6ff18675"}个人号不能使用这个接口
        } catch (Exception e) {
            log.error("推送客服消息异常:{},stack:{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    private AccessTokenRes readTokenFromFile() {
        File file = new File("wx-token.json");
        if (file.exists()) {
            try {
                AccessTokenRes accessTokenRes1 = cruxSerializer.deserialize(IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8), AccessTokenRes.class);
                if (accessTokenRes1 != null) {
                    if (accessTokenRes1.getErrcode() == 0 && StringUtils.isNoneBlank(accessTokenRes1.getAccessToken())) {
                        return accessTokenRes1;
                    }
                }
            } catch (IOException e) {
                //ignore
            }
        }
        return null;
    }

    private boolean checkExpired(AccessTokenRes accessTokenRes1) {
        if (accessTokenRes1.getCreatedTime() + accessTokenRes1.getExpiresIn() >= (System.currentTimeMillis() / 1000)) {
            log.debug("wx token 已经过期");
            return true;
        } else {
            return false;
        }
    }

    private AccessTokenRes readTokenFromHttp() {
        AccessTokenRes accessTokenRes1 = new AccessTokenRes();
        File file = new File("wx-token.json");
        //https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Get_access_token.html
        WXVo wx = appConfig.getApp().getWx();
        try {
            accessTokenRes1 = myHttpClient.doGet(baseUrl + "/token?grant_type=client_credential&appid=" + wx.getAppId() + "&secret=" + wx.getAppSecret(), Collections.emptyList(), AccessTokenRes.class);
            if (accessTokenRes1 != null && accessTokenRes1.getErrcode() == 0) {
                accessTokenRes1.setCreatedTime(System.currentTimeMillis() / 1000);
                String config = cruxSerializer.serialize(accessTokenRes1);
                IOUtils.write(config, new FileWriter(file, false));
            } else {
                throw new Exception("token获取失败:" + accessTokenRes1);
            }
            return accessTokenRes1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getAccessToken() {
        if (accessTokenRes == null) {
            accessTokenRes = readTokenFromFile();
            if (accessTokenRes != null) {
                if (checkExpired(accessTokenRes)) {
                    accessTokenRes = readTokenFromHttp();
                }
            } else {
                accessTokenRes = readTokenFromHttp();
            }
        } else {
            if (checkExpired(accessTokenRes)) {
                accessTokenRes = readTokenFromHttp();
            }
        }
        return accessTokenRes.getAccessToken();
    }
}
