package org.ligson.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.ligson.chat.impl.OpenAIChatServiceImpl;
import org.ligson.chat.impl.TuringChatServiceImpl;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.http.MsgTemplate;
import org.ligson.http.ReplyMsg;
import org.ligson.util.MyHttpClient;
import org.ligson.vo.AppConfig;
import org.ligson.wx.WXClient;
import org.ligson.wx.vo.ReceivingStdMsgVo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@BootService
@Slf4j
public class WxHandler implements HttpHandler {
    @BootAutowired
    private AppConfig appConfig;
    @BootAutowired
    private TuringChatServiceImpl turingChatService;
    @BootAutowired
    private OpenAIChatServiceImpl openAIChatService;
    @BootAutowired
    private WXClient wxClient;
    @BootAutowired
    private MyHttpClient myHttpClient;
    @BootAutowired
    private MsgTemplate msgTemplate;
    private static final Executor executor = Executors.newCachedThreadPool();

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

    private List<String> replyImgThread(String str) {
        return openAIChatService.imageGenerate(str);
    }

    private String replyThread(String str) {
        String content = null;
        if (!StringUtils.isBlank(str)) {
            if (str.startsWith(appConfig.getApp().getTuring().getKeyword())) {
                String con = str.replaceFirst(appConfig.getApp().getTuring().getKeyword(), "");
                content = turingChatService.chat(con);
            } else if (str.startsWith(appConfig.getApp().getOpenai().getKeyword())) {
                String con = str.replaceFirst(appConfig.getApp().getOpenai().getKeyword(), "");
                content = openAIChatService.chat(con);
            } else {
                content = "????????????" + appConfig.getApp().getTuring().getKeyword() + ",????????????????????????;" + "????????????" + appConfig.getApp().getOpenai().getKeyword() + ",?????????OpenAI?????????;5s?????????????????????????????????????????????";
            }
        }
        return content;
    }

    private void pushMsg2Wx(String toUser, String msg) {
        //https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN
        wxClient.pushMsg(toUser, msg);
    }

    private ReplyMsg replyText(String toUser, String question, String msgId) {
        long startTime = System.currentTimeMillis();
        ReplyMsg replyMsg = new ReplyMsg();
        // ?????????????????????3???
        long timeout = 4800;
        // ??????????????????????????????????????????????????????????????????
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);
        Future<String> future = completionService.submit(() -> replyThread(question));
        try {
            Future<String> result = completionService.poll(timeout, TimeUnit.MILLISECONDS);
            if (result == null) {
                long endTime = System.currentTimeMillis();
                log.warn("??????????????????,?????????{}s", (endTime - startTime) / 1000.0);
                replyMsg.setTimeout(true);
                executor.execute(() -> {
                    while (true) {
                        if (future.isDone()) {
                            break;
                        }
                    }
                    long endTime2 = System.currentTimeMillis();
                    log.debug("?????????????????????:{}s", (endTime2 - startTime) / 1000.0);
                    try {
                        String askMsg = future.get();
                        msgTemplate.writeMsg(toUser, msgId, question, askMsg);
                        pushMsg2Wx(toUser, askMsg);
                        log.info("?????????????????????:{}", askMsg);
                    } catch (Exception e) {
                        log.error("??????????????????...:{},stack:{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
                        msgTemplate.writeMsg(toUser, msgId, question, "??????????????????," + e.getMessage());
                    }
                    future.cancel(true);
                });
            } else {
                replyMsg.setMsg(result.get());
                msgTemplate.writeMsg(toUser, msgId, question, replyMsg.getMsg());
            }
        } catch (Exception e) {
            future.cancel(true);
            log.error("????????????:" + e.getMessage(), e);
            replyMsg.setTimeout(false);

        }
        return replyMsg;
    }

    private String imgMsg(ReceivingStdMsgVo receivingStdMsgVo, HttpExchange exchange) throws IOException {
        String con = receivingStdMsgVo.getContent().replaceFirst(appConfig.getApp().getOpenai().getKeyword(), "");
        // ?????????????????????3???
        long timeout = 4800;
        // ??????????????????????????????????????????????????????????????????
        CompletionService<List<String>> completionService = new ExecutorCompletionService<>(executor);
        Future<List<String>> future = completionService.submit(() -> replyImgThread(receivingStdMsgVo.getContent()));
        String msg = null;

        Future<List<String>> result = null;
        try {
            result = completionService.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            msg = e.getMessage();
        }
        if (result == null) {
            msg = "????????????????????????...5s?????????????????????,????????????????????????,???????????????" + appConfig.getApp().getServer().getDomainUrl() + "/msg/" + receivingStdMsgVo.getFromUserName() + "/" + receivingStdMsgVo.getMsgId();
            //msgTemplate.writeMsg(receivingStdMsgVo.getMsgId(), receivingStdMsgVo.getContent(), "????????????");
            executor.execute(() -> {
                while (true) {
                    if (future.isDone()) {
                        break;
                    }
                }
                try {
                    List<String> urls = future.get();
                    if (!urls.isEmpty()) {
                        File destFile = myHttpClient.download(urls.get(0), "0", appConfig.getApp().getWx().getMsgPath() + "/" + receivingStdMsgVo.getFromUserName() + "/" + receivingStdMsgVo.getMsgId());
                        if (destFile != null) {
                            String imgUrl = appConfig.getApp().getServer().getDomainUrl() + "/msg-img/" + receivingStdMsgVo.getFromUserName() + "/" + receivingStdMsgVo.getMsgId() + "/" + destFile.getName();
                            msgTemplate.writeMsg(receivingStdMsgVo.getFromUserName(), receivingStdMsgVo.getMsgId(), receivingStdMsgVo.getContent(), "<img src='" + imgUrl + "'/>");
                        } else {
                            msgTemplate.writeMsg(receivingStdMsgVo.getFromUserName(), receivingStdMsgVo.getMsgId(), receivingStdMsgVo.getContent(), "????????????" + urls.get(0) + "??????");
                        }
                    } else {
                        msgTemplate.writeMsg(receivingStdMsgVo.getFromUserName(), receivingStdMsgVo.getMsgId(), receivingStdMsgVo.getContent(), "??????????????????");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    msgTemplate.writeMsg(receivingStdMsgVo.getFromUserName(), receivingStdMsgVo.getMsgId(), receivingStdMsgVo.getContent(), e.getMessage());
                }
            });

        } else {
            List<String> urls;
            try {
                urls = result.get();
                if (!urls.isEmpty()) {
                    return wxClient.buildReplyImgMsg(receivingStdMsgVo.getToUserName(), receivingStdMsgVo.getFromUserName(), urls.get(0), receivingStdMsgVo.getMsgId());
                } else {
                    msg = "?????????????????????";
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                msg = e.getMessage();
            }
        }

        return wxClient.buildReplyTextMsg(receivingStdMsgVo.getToUserName(), receivingStdMsgVo.getFromUserName(), msg);
    }

    private String textMsg(ReceivingStdMsgVo receivingStdMsgVo, HttpExchange exchange) throws IOException {
        ReplyMsg replyMsg = replyText(receivingStdMsgVo.getFromUserName(), receivingStdMsgVo.getContent(), receivingStdMsgVo.getMsgId());
        String msg;
        if (replyMsg.isTimeout()) {
            msg = "????????????????????????...5s?????????????????????,????????????????????????,???????????????" + appConfig.getApp().getServer().getDomainUrl() + "/msg/" + receivingStdMsgVo.getFromUserName() + "/" + receivingStdMsgVo.getMsgId();
        } else {
            msg = replyMsg.getMsg();
        }
        if (!StringUtils.isBlank(msg)) {
            return wxClient.buildReplyTextMsg(receivingStdMsgVo.getToUserName(), receivingStdMsgVo.getFromUserName(), msg);
        } else {
            log.warn("??????????????????");
            return null;
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Access_Overview.html
        log.debug("????????????:{},??????:{}", exchange.getRequestURI(), exchange.getRequestMethod());
        Map<String, String> param = queryToMap(exchange.getRequestURI().getQuery());
        String signature = param.get("signature");
        String echostr = param.get("echostr");
        String timestamp = param.get("timestamp");
        String nonce = param.get("nonce");
        String openid = param.get("openid");
        String token = appConfig.getApp().getWx().getToken();


        String[] arr = new String[]{token, timestamp, nonce};
        // ???token???timestamp???nonce?????????????????????????????????
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (String s : arr) {
            content.append(s);
        }
        String str = content.toString();
        String sign = DigestUtils.sha1Hex(str);

        boolean signVer = sign.equalsIgnoreCase(signature);
        if (sign.equalsIgnoreCase(signature)) {
            log.debug("??????????????????");
        } else {
            log.debug("??????????????????,????????????:{},????????????:{}", signature, sign);
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
            String xml = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
            log.debug("??????????????????{}", xml);
            ReceivingStdMsgVo receivingStdMsgVo = wxClient.receivingStdMsg(xml);
            if (receivingStdMsgVo != null) {
                String reply = null;
                if (receivingStdMsgVo.getContent().contains("??????") || receivingStdMsgVo.getContent().contains("??????")) {
                    reply = imgMsg(receivingStdMsgVo, exchange);
                } else {
                    reply = textMsg(receivingStdMsgVo, exchange);
                }
                if (reply != null) {
                    byte[] replyBuf = reply.getBytes();
                    exchange.sendResponseHeaders(200, replyBuf.length);
                    exchange.getResponseBody().write(replyBuf);
                } else {
                    exchange.sendResponseHeaders(200, 0);
                }
            } else {
                exchange.sendResponseHeaders(200, 0);
            }
        }
        exchange.close();
    }
}
