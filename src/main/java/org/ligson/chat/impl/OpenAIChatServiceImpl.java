package org.ligson.chat.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ligson.chat.ChatService;
import org.ligson.images.GenerateImageService;
import org.ligson.openai.OpenAiClient;
import org.ligson.openai.vo.Model;
import org.ligson.openai.vo.ModelResult;
import org.ligson.openai.vo.req.*;
import org.ligson.openai.vo.res.*;
import org.ligson.serializer.CruxSerializer;
import org.ligson.util.MyHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OpenAIChatServiceImpl implements ChatService {
    private OpenAiClient openAiClient;
    @Autowired
    private CruxSerializer cruxSerializer;
    @Autowired
    private MyHttpClient myHttpClient;
    @Value("${app.openai.sk-token}")
    private String skToken;
    @Value("${app.openai.img-dir}")
    private String imgDir;
    @Value("${app.server.domain-url}")
    private String domainUrl;
    @Autowired
    private GenerateImageService imageService;

    public OpenAIChatServiceImpl() {
        log.debug("---");
    }

    @PostConstruct
    public void init() {
        this.openAiClient = new OpenAiClient(skToken, myHttpClient, cruxSerializer);
        log.debug("openAiClient is :{}", this.openAiClient.hashCode());
    }

    public List<String> models() {
        ModelResult result = openAiClient.models();
        return result.getData().stream().map(Model::getId).collect(Collectors.toList());
    }

    private String chatTxt(ChatCompletionsReq completionsReq) {
        ChatCompletionsRes res = openAiClient.chatCompletions(completionsReq);
        if (res != null) {
            if (!res.getChoices().isEmpty()) {
                StringBuilder stringBuffer = new StringBuilder();
                for (ChatChoice choice : res.getChoices()) {
                    stringBuffer.append(choice.getMessage().getContent());
                }
                return stringBuffer.toString();
            }
        }
        return null;
    }

    public String chat(ChatCompletionsReq completionsReq) {
        List<Message> messages = completionsReq.getMessages();
        if (messages.isEmpty()) {
            return null;
        }
        Message message = messages.get(messages.size() - 1);
        if (message.getContent().contains("图片")) {
            return chatImg(completionsReq, message.getContent());
        } else {
            return chatTxt(completionsReq);
        }
    }

    private String chatImg(ChatCompletionsReq completionsReq, String msg) {
        String imageUrl = imageService.imageGenerate(msg);
        StringBuilder builder = new StringBuilder();
        if (imageUrl != null) {
            List<String> urls = Collections.singletonList(imageUrl);
            for (String url : urls) {
                File file;
                try {
                    file = myHttpClient.download(url, UUID.randomUUID().toString(), imgDir + "user-images");
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    continue;
                }
                String imgUrl = domainUrl + "/user-images/" + file.getName();
                builder.append("<img src='").append(imgUrl).append("'/>").append("<br/>");
            }
        }
        return builder.toString();
    }

    @Override
    public String chat(String contextId, String question) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        CompletionsReq completionsReq = new CompletionsReq();
        completionsReq.setModel("text-davinci-003");
        completionsReq.setPrompt(question);
        if (StringUtils.isNotBlank(contextId)) {
            ReqContext reqContext = new ReqContext();
            reqContext.setConversationId(contextId);
            //completionsReq.setContext(reqContext);
        }
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
