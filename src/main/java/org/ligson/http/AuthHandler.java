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
import org.ligson.wx.WXClient;
import org.ligson.wx.vo.ReceivingStdMsgVo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class AuthHandler implements HttpHandler {
    private AppConfig appConfig;
    private TuringChatServiceImpl turingChatService;
    private OpenAIChatServiceImpl openAIChatService;
    private WXClient wxClient;
    private static final Executor executor = Executors.newCachedThreadPool();

    public AuthHandler() {
        try {
            appConfig = AppConfig.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        turingChatService = new TuringChatServiceImpl();
        openAIChatService = new OpenAIChatServiceImpl();
        wxClient = new WXClient();
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
                content = "消息开头" + appConfig.getApp().getTuring().getKeyword() + ",会使用图灵机器人;" + "消息开头" + appConfig.getApp().getOpenai().getKeyword() + ",会使用OpenAI机器人;5s没有返回或者服务器错误请重试。";
            }
        }
        return content;
    }

    private void pushMsg2Wx(String toUser, String msg) {
        //https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN
        wxClient.pushMsg(toUser, msg);
    }

    private ReplyMsg reply(String toUser, String question, String msgId) {
        long startTime = System.currentTimeMillis();
        ReplyMsg replyMsg = new ReplyMsg();
        // 定义超时时间为3秒
        long timeout = 4800;
        // 创建一个新的线程池，用于执行要限制时间的方法
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);
        Future<String> future = completionService.submit(() -> replyThread(question));
        try {
            Future<String> result = completionService.poll(timeout, TimeUnit.MILLISECONDS);
            if (result == null) {
                long endTime = System.currentTimeMillis();
                log.warn("调用接口超时,耗时：{}s", (endTime - startTime) / 1000.0);
                replyMsg.setTimeout(true);
                executor.execute(() -> {
                    while (true) {
                        if (future.isDone()) {
                            break;
                        }
                    }
                    long endTime2 = System.currentTimeMillis();
                    log.debug("接口完成，耗时:{}s", (endTime2 - startTime) / 1000.0);
                    try {
                        String askMsg = future.get();
                        MsgTemplate.writeMsg(msgId, question, askMsg);
                        pushMsg2Wx(toUser, askMsg);
                        log.info("主动推送给信息:{}", askMsg);
                    } catch (Exception e) {
                        log.error("调用接口异常...:{}", e.getMessage());
                        MsgTemplate.writeMsg(msgId, question, "调用接口异常," + e.getMessage());
                    }
                    future.cancel(true);
                });
            } else {
                replyMsg.setMsg(result.get());
                MsgTemplate.writeMsg(msgId, question, replyMsg.getMsg());
            }
        } catch (Exception e) {
            future.cancel(true);
            log.error("线程异常:" + e.getMessage(), e);
            replyMsg.setTimeout(false);

        }
        return replyMsg;
    }

    private void imgMsg(ReceivingStdMsgVo receivingStdMsgVo, HttpExchange exchange) throws IOException {
        String con = receivingStdMsgVo.getContent().replaceFirst(appConfig.getApp().getOpenai().getKeyword(), "");
        // 定义超时时间为3秒
        long timeout = 4800;
        // 创建一个新的线程池，用于执行要限制时间的方法
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
            msg = "机器人正在思考中...5s没返回，请重试,或者点击链接查看,需要等待，" + appConfig.getApp().getServer().getDomainUrl() + "/msg/" + receivingStdMsgVo.getMsgId();
            //MsgTemplate.writeMsg(receivingStdMsgVo.getMsgId(), receivingStdMsgVo.getContent(), "正在生成");
            executor.execute(() -> {
                while (true) {
                    if (future.isDone()) {
                        break;
                    }
                }
                try {
                    List<String> urls = future.get();
                    if (!urls.isEmpty()) {
                        MsgTemplate.writeMsg(receivingStdMsgVo.getMsgId(), receivingStdMsgVo.getContent(), "<img src='" + urls.get(0) + "'/>");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });

        } else {
            List<String> urls = null;
            try {
                urls = result.get();
                if (!urls.isEmpty()) {
                    String reply = wxClient.buildReplyImgMsg(receivingStdMsgVo.getToUserName(), receivingStdMsgVo.getFromUserName(), urls.get(0), receivingStdMsgVo.getMsgId());
                    byte[] replyBuf = reply.getBytes();
                    exchange.sendResponseHeaders(200, replyBuf.length);
                    exchange.getResponseBody().write(replyBuf);
                    return;
                } else {
                    msg = "没有合适的图片";
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                msg = e.getMessage();
            }
        }

        String reply = wxClient.buildReplyTextMsg(receivingStdMsgVo.getToUserName(), receivingStdMsgVo.getFromUserName(), msg);
        byte[] replyBuf = reply.getBytes();
        exchange.sendResponseHeaders(200, replyBuf.length);
        exchange.getResponseBody().write(replyBuf);
    }

    private void textMsg(ReceivingStdMsgVo receivingStdMsgVo, HttpExchange exchange) throws IOException {
        ReplyMsg replyMsg = reply(receivingStdMsgVo.getFromUserName(), receivingStdMsgVo.getContent(), receivingStdMsgVo.getMsgId());
        String msg;
        if (replyMsg.isTimeout()) {
            msg = "机器人正在思考中...5s没返回，请重试,或者点击链接查看,需要等待，" + appConfig.getApp().getServer().getDomainUrl() + "/msg/" + receivingStdMsgVo.getMsgId();
        } else {
            msg = replyMsg.getMsg();
        }
        if (!StringUtils.isBlank(msg)) {
            String reply = wxClient.buildReplyTextMsg(receivingStdMsgVo.getToUserName(), receivingStdMsgVo.getFromUserName(), msg);
            byte[] replyBuf = reply.getBytes();
            exchange.sendResponseHeaders(200, replyBuf.length);
            exchange.getResponseBody().write(replyBuf);
        } else {
            log.warn("空内容不恢复");
            exchange.sendResponseHeaders(200, 0);
        }
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
            String xml = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
            log.debug("接收到内容：{}", xml);
            ReceivingStdMsgVo receivingStdMsgVo = wxClient.receivingStdMsg(xml);
            if (receivingStdMsgVo != null) {
                if (receivingStdMsgVo.getContent().contains("图片")) {
                    imgMsg(receivingStdMsgVo, exchange);
                } else {
                    textMsg(receivingStdMsgVo, exchange);
                }
            } else {
                exchange.sendResponseHeaders(200, 0);
            }
        }
        exchange.close();
    }
}
