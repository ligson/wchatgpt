package org.ligson.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.ligson.chat.impl.OpenAIChatServiceImpl;
import org.ligson.chat.impl.TuringChatServiceImpl;
import org.ligson.vo.AppConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AuthHandler implements HttpHandler {
    private AppConfig appConfig;
    private TuringChatServiceImpl turingChatService;
    private OpenAIChatServiceImpl openAIChatService;

    public AuthHandler() {
        try {
            appConfig = AppConfig.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        turingChatService = new TuringChatServiceImpl();
        openAIChatService = new OpenAIChatServiceImpl();
    }

    public static Map<String, String> queryToMap(String query) {

        Map<String, String> result = new HashMap<>();

        for (String param : query.split("&")) {

            String pair[] = param.split("=");

            if (pair.length > 1) {

                result.put(pair[0], pair[1]);

            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    private String reply(String str) {
        String content = null;
        if (!StringUtils.isBlank(str)) {
            if (str.startsWith(appConfig.getApp().getTuring().getKeyword())) {
                String con = str.replaceFirst(appConfig.getApp().getTuring().getKeyword(), "");
                content = turingChatService.chat(con);
            } else if (str.startsWith(appConfig.getApp().getOpenai().getKeyword())) {
                String con = str.replaceFirst(appConfig.getApp().getOpenai().getKeyword(), "");
                content = openAIChatService.chat(con);
            } else {
                content = "消息开头" + appConfig.getApp().getTuring().getKeyword() + ",会使用图灵机器人;" +
                        "消息开头" + appConfig.getApp().getOpenai().getKeyword() + ",会使用OpenAI机器人;5s没有返回或者服务器错误请重试。";
            }
        }
        return content;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Access_Overview.html
        log.debug("请求地址:{},方法:{}", exchange.getRequestURI(), exchange.getRequestMethod());
        Map<String, String> param = queryToMap(exchange.getRequestURI().getQuery());
        String signature = param.get("signature");
        String echostr = param.get("echostr");
        String timestamp = param.get("timestamp");
        String nonce = param.get("nonce");
        String openid = param.get("openid");
        String token = appConfig.getApp().getWx().getToken();


        String[] arr = new String[]{token, timestamp, nonce};
        // 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (String s : arr) {
            content.append(s);
        }
        String str = content.toString();
        String sign = DigestUtils.sha1Hex(str);

        boolean signVer = sign.equalsIgnoreCase(signature);
        if (sign.equalsIgnoreCase(signature)) {
            log.debug("验证签名成功");
        } else {
            log.debug("验证签名失败,微信签名:{},自验签名:{}", signature, sign);
        }
        if ("get".equalsIgnoreCase(exchange.getRequestMethod())) {
            if (signVer) {
                byte[] echostrBuf = echostr.getBytes();
                exchange.sendResponseHeaders(200, echostrBuf.length);
                exchange.getResponseBody().write(echostrBuf);
            } else {
                exchange.sendResponseHeaders(500, 0);
            }
        } else if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
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
            String xml = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
            log.debug("接收到内容：{}", xml);
            try {
                Document doc = DocumentHelper.parseText(xml);
                String ToUserName = doc.selectSingleNode("/xml/ToUserName").getText();
                String FromUserName = doc.selectSingleNode("/xml/FromUserName").getText();
                String CreateTime = doc.selectSingleNode("/xml/CreateTime").getText();
                String MsgType = doc.selectSingleNode("/xml/MsgType").getText();
                String Content = doc.selectSingleNode("/xml/Content").getText();
                String MsgId = doc.selectSingleNode("/xml/MsgId").getText();

                String con = reply(Content);
                if (!StringUtils.isBlank(con)) {
                    String reply = "<xml>" +
                            "<ToUserName><![CDATA[" + FromUserName + "]]></ToUserName>" +
                            "<FromUserName><![CDATA[" + ToUserName + "]]></FromUserName>" +
                            "<CreateTime>" + System.currentTimeMillis() + "</CreateTime>" +
                            "<MsgType><![CDATA[text]]></MsgType>" +
                            "<Content><![CDATA[" + con + "]]></Content>" +
                            "</xml>";
                    byte[] replyBuf = reply.getBytes();
                    exchange.sendResponseHeaders(200, replyBuf.length);
                    exchange.getResponseBody().write(replyBuf);
                } else {
                    log.warn("空内容不恢复");
                    exchange.sendResponseHeaders(200, 0);
                }

            } catch (DocumentException e) {
                log.error("解析xml:{}失败:{},trace:{}", xml, e.getMessage(), ExceptionUtils.getStackTrace(e));
                exchange.sendResponseHeaders(200, 0);
            }
        }
        exchange.close();
    }
}
