package org.ligson.wx;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.ligson.serializer.CruxSerializer;
import org.ligson.util.MyHttpClient;
import org.ligson.wx.vo.AccessTokenRes;
import org.ligson.wx.vo.CustomMsg;
import org.ligson.wx.vo.CustomMsgRes;
import org.ligson.wx.vo.ReceivingStdMsgVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
@Slf4j
public class WXClient {
    private static final String baseUrl = "https://api.weixin.qq.com/cgi-bin";
    @Autowired
    private MyHttpClient myHttpClient;
    private static AccessTokenRes accessTokenRes;
    @Autowired
    private CruxSerializer cruxSerializer;

    @Value("${app.wx.app-id}")
    private String appId;
    @Value("${app.wx.app-secret}")
    private String appSecret;


    public void pushMsg(String toUser, String msg) {
        if (StringUtils.isBlank(msg)) {
            log.debug("消息是空不进行推送");
            return;
        }
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

    public ReceivingStdMsgVo receivingStdMsg(String xml) {
        //https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Receiving_standard_messages.html
        /***
         * 当普通微信用户向公众账号发消息时，微信服务器将 POST 消息的 XML 数据包到开发者填写的 URL 上。
         * 请注意：
         * 关于重试的消息排重，推荐使用 msgid 排重。
         * 微信服务器在五秒内收不到响应会断掉连接，并且重新发起请求，总共重试三次。假如服务器无法保证在五秒内处理并回复，可以直接回复空串，微信服务器不会对此作任何处理，并且不会发起重试。详情请见“发送消息 - 被动回复消息”。
         * 如果开发者需要对用户消息在5秒内立即做出回应，即使用“发送消息 - 被动回复消息”接口向用户被动回复消息时，可以在
         * 公众平台官网的开发者中心处设置消息加密。开启加密后，用户发来的消息和开发者回复的消息都会被加密（但开发者通过客服接口等 API 调用形式向用户发送消息，则不受影响）。关于消息加解密的详细说明，请见“发送消息 - 被动回复消息加解密说明”。
         *
         * https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Passive_user_reply_message.html
         */
        log.debug("接收到内容：{}", xml);
        try {
            Document doc = DocumentHelper.parseText(xml);
            String ToUserName = doc.selectSingleNode("/xml/ToUserName").getText();
            String FromUserName = doc.selectSingleNode("/xml/FromUserName").getText();
            String CreateTime = doc.selectSingleNode("/xml/CreateTime").getText();
            String MsgType = doc.selectSingleNode("/xml/MsgType").getText();
            //TODO 目前支能处理文本
            if (!"text".equals(MsgType)) {
                return null;
            }
            String Content = doc.selectSingleNode("/xml/Content").getText();
            String MsgId = doc.selectSingleNode("/xml/MsgId").getText();
            ReceivingStdMsgVo receivingStdMsgVo = new ReceivingStdMsgVo(ToUserName, FromUserName, Long.parseLong(CreateTime), MsgType, Content, MsgId);
            return receivingStdMsgVo;
        } catch (Exception e) {
            log.error("解析xml:{}失败:{},trace:{}", xml, e.getMessage(), ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    public String buildReplyTextMsg(String fromUserName, String toUserName, String msg) {
        return "<xml>" +
                "<ToUserName><![CDATA[" + toUserName + "]]></ToUserName>" +
                "<FromUserName><![CDATA[" + fromUserName + "]]></FromUserName>" +
                "<CreateTime>" + System.currentTimeMillis() + "</CreateTime>" +
                "<MsgType><![CDATA[text]]></MsgType>" +
                "<Content><![CDATA[" + msg + "]]></Content>" +
                "</xml>";
    }

    public String buildReplyImgMsg(String fromUserName, String toUserName, String url, String msgId) {
        String msg = "<xml>" +
                "<ToUserName><![CDATA[" + toUserName + "]]></ToUserName>" +
                "<FromUserName><![CDATA[" + fromUserName + "]]></FromUserName>" +
                "<CreateTime>" + System.currentTimeMillis() + "</CreateTime>" +
                "<MsgType><![CDATA[image]]></MsgType>" +
                "<PicUrl><![CDATA[" + url + "]]></PicUrl>" +
                //"<MediaId><![CDATA[media_id]]></MediaId>" +
                "<MsgId>" + msgId + "</MsgId>" +
                //"<MsgDataId>xxxx</MsgDataId>" +
                //"<Idx>xxxx</Idx>" +
                "</xml>";
        return msg;
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
        try {
            accessTokenRes1 = myHttpClient.doGet(baseUrl + "/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret, Collections.emptyList(), AccessTokenRes.class);
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
