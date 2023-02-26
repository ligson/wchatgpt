package org.ligson.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.ligson.vo.AppConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MsgTemplate {
    public static void writeMsg(String msgId, String question, String ask) {
        String string = null;
        try {
            InputStream tmpInput = MsgTemplate.class.getClassLoader().getResourceAsStream("MsgTmp.html");
            string = IOUtils.toString(tmpInput, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.debug("模板内容:{}", string);
        string = string.replaceAll("\\{\\{ask}}", ask);
        string = string.replaceAll("\\{\\{question}}", question);
        log.debug("模板替换后:{}", string);
        File msgDir = null;
        try {
            msgDir = new File(AppConfig.getInstance().getApp().getWx().getMsgPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!msgDir.exists()) {
            msgDir.mkdirs();
        }
        File msgFile = new File(msgDir, msgId + ".html");
        try {
            FileOutputStream fos = new FileOutputStream(msgFile);
            IOUtils.write(string, fos, StandardCharsets.UTF_8);
            fos.close();
            log.debug("消息{}写入{}成功,文件大小：{}", msgId, msgFile.getAbsolutePath(), msgFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMsgHtml(String msgId) {
        String msgDir = null;
        try {
            msgDir = AppConfig.getInstance().getApp().getWx().getMsgPath();
        } catch (IOException e) {
            return null;
        }
        File msgFile = new File(msgDir, msgId + ".html");
        if (!msgFile.exists()) {
            return null;
        } else {
            try {
                return IOUtils.toString(msgFile.toURI(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
