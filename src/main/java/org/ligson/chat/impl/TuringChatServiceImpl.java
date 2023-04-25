package org.ligson.chat.impl;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.ligson.chat.ChatService;
import org.ligson.serializer.CruxSerializer;
import org.ligson.turing.TuringClient;
import org.ligson.turing.vo.res.Result;
import org.ligson.turing.vo.res.TuringResult;
import org.ligson.util.MyHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TuringChatServiceImpl implements ChatService {

    private TuringClient turingClient;

    @Autowired
    private MyHttpClient myHttpClient;
    @Autowired
    private CruxSerializer cruxSerializer;

    @Value("${app.turing.user-id}")
    private String userId;
    @Value("${app.turing.api-key}")
    private String apiKey;

    @PostConstruct
    @SneakyThrows
    public void init() {
        this.turingClient = new TuringClient(userId,
                apiKey,
                myHttpClient,
                cruxSerializer);
    }

    @Override
    public String chat(String contextId, String question) {
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
