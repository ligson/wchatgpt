package org.ligson.thread;

import org.ligson.openai.OpenAiClient;
import org.ligson.openai.vo.req.ImgGenReq;
import org.ligson.openai.vo.res.ImgGenRes;
import org.ligson.vo.AppConfig;

public class OpenAiTest {
    public static void main(String[] args) throws Exception {
        String token = AppConfig.getInstance().getApp().getOpenai().getSkToken();
        OpenAiClient aiClient = new OpenAiClient(token);
        ImgGenReq imgGenReq = new ImgGenReq();
        imgGenReq.setPrompt("夕阳下的大海，海豚在跳跃");
        ImgGenRes st = aiClient.imageGenerations(imgGenReq);
        System.out.println(st);

    }
}
