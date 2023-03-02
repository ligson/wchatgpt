package org.ligson.chat.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ligson.chat.ChatService;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.openai.OpenAiClient;
import org.ligson.openai.vo.req.CompletionsReq;
import org.ligson.openai.vo.req.ImgGenReq;
import org.ligson.openai.vo.res.Choice;
import org.ligson.openai.vo.res.CompletionsRes;
import org.ligson.openai.vo.res.ImgGenData;
import org.ligson.openai.vo.res.ImgGenRes;
import org.ligson.serializer.CruxSerializer;
import org.ligson.util.MyHttpClient;
import org.ligson.vo.AppConfig;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@BootService(initMethod = "init")
@Slf4j
public class OpenAIChatServiceImpl implements ChatService {
    private OpenAiClient openAiClient;

    @BootAutowired
    private AppConfig appConfig;
    @BootAutowired
    private CruxSerializer cruxSerializer;
    @BootAutowired
    private MyHttpClient myHttpClient;

    public OpenAIChatServiceImpl() {
        log.debug("---");
    }

    public void init() {
        this.openAiClient = new OpenAiClient(appConfig.getApp().getOpenai().getSkToken(), myHttpClient, cruxSerializer);
        log.debug("openAiClient is :{}", this.openAiClient.hashCode());
    }

    @Override
    public String chat(String question) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        CompletionsReq completionsReq = new CompletionsReq();
        completionsReq.setModel("text-davinci-003");
        completionsReq.setPrompt(question);
        CompletionsRes res = openAiClient.completions(completionsReq);
        if (res != null) {
            if (!res.getChoices().isEmpty()) {
                StringBuilder stringBuffer = new StringBuilder();
                for (Choice choice : res.getChoices()) {
                    stringBuffer.append(choice.getText());
                }
                return stringBuffer.toString();
            }
        }
        return null;
    }

    public List<String> imageGenerate(String str) {
        ImgGenReq imgGenReq = new ImgGenReq();
        imgGenReq.setPrompt(str);
        ImgGenRes res = openAiClient.imageGenerations(imgGenReq);
        if (res == null) {
            return Collections.EMPTY_LIST;
        }
        return res.getData().stream().map(ImgGenData::getUrl).collect(Collectors.toList());
    }
}
