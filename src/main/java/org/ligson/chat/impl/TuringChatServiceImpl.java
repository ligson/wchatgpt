package org.ligson.chat.impl;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.ligson.chat.ChatService;
import org.ligson.turing.TuringClient;
import org.ligson.turing.vo.res.Result;
import org.ligson.turing.vo.res.TuringResult;
import org.ligson.vo.AppConfig;

public class TuringChatServiceImpl implements ChatService {
    private TuringClient turingClient;
    private AppConfig appConfig;

    @SneakyThrows
    public TuringChatServiceImpl() {
        appConfig = AppConfig.getInstance();
        turingClient = new TuringClient(appConfig.getApp().getTuring().getUserId(), appConfig.getApp().getTuring().getApiKey());
    }

    @Override
    public String chat(String question) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        TuringResult turingResult = turingClient.chat(question);
        if (turingResult.getIntent().getCode() == 0) {
            if (!turingResult.getResults().isEmpty()) {
                StringBuilder builder = new StringBuilder();
                for (Result result : turingResult.getResults()) {
                    builder.append(result.getValues().getText());
                }
                return builder.toString();
            }
        }
        return null;
    }
}
