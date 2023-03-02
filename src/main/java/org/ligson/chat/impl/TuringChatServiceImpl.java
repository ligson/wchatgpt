package org.ligson.chat.impl;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.ligson.chat.ChatService;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.serializer.CruxSerializer;
import org.ligson.turing.TuringClient;
import org.ligson.turing.vo.res.Result;
import org.ligson.turing.vo.res.TuringResult;
import org.ligson.util.MyHttpClient;
import org.ligson.vo.AppConfig;

@BootService(initMethod = "init")
public class TuringChatServiceImpl implements ChatService {

    private TuringClient turingClient;
    @BootAutowired
    private AppConfig appConfig;

    @BootAutowired
    private MyHttpClient myHttpClient;
    @BootAutowired
    private CruxSerializer cruxSerializer;

    @SneakyThrows
    public void init() {
        turingClient = new TuringClient(appConfig.getApp().getTuring().getUserId(),
                appConfig.getApp().getTuring().getApiKey(),
                myHttpClient,
                cruxSerializer);
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
