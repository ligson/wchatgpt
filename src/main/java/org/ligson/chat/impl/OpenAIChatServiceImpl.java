package org.ligson.chat.impl;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.ligson.chat.ChatService;
import org.ligson.openai.OpenAiClient;
import org.ligson.openai.vo.req.CompletionsReq;
import org.ligson.openai.vo.res.Choice;
import org.ligson.openai.vo.res.CompletionsRes;
import org.ligson.vo.AppConfig;

public class OpenAIChatServiceImpl implements ChatService {
    private OpenAiClient openAiClient;
    private AppConfig appConfig;

    @SneakyThrows
    public OpenAIChatServiceImpl() {
        appConfig = AppConfig.getInstance();
        openAiClient = new OpenAiClient(appConfig.getApp().getOpenai().getSkToken());
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
}
