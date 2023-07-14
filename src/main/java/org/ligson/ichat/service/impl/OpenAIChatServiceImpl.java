package org.ligson.ichat.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ligson.ichat.fw.serializer.CruxSerializer;
import org.ligson.ichat.fw.simplecrud.vo.WebResult;
import org.ligson.ichat.images.GenerateImageService;
import org.ligson.ichat.openai.OpenAiClient;
import org.ligson.ichat.openai.vo.Model;
import org.ligson.ichat.openai.vo.ModelResult;
import org.ligson.ichat.openai.vo.req.*;
import org.ligson.ichat.openai.vo.res.*;
import org.ligson.ichat.service.ChatService;
import org.ligson.ichat.util.MyHttpClient;
import org.ligson.ichat.util.WindowSizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
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
    @Autowired
    private WindowSizeUtil windowSizeUtil;

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
        return chatTxt(completionsReq);
    }

    public String img(String contextId, String question) {
        String imgUrl = generateImgReturnUrl(question);
        StringBuilder builder = new StringBuilder();
        if (imgUrl != null) {
            builder.append("<img src='").append(imgUrl).append("' ").append(windowSizeUtil.getStyle()).append("/>").append("<br/>");
        }
        return builder.toString();
    }

    public String generateImgReturnUrl(String question) {
        String image = imageService.imageGenerate(question);
        return image != null ? getWebImageUrl(image) : null;
    }

    private String getWebImageUrl(String imageUrl) {
        File file = myHttpClient.download(imageUrl, UUID.randomUUID().toString(), imgDir + "user-images");
        return file != null ? domainUrl + "/user-images/" + file.getName() : null;
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

    public WebResult audio2txt(File file) {
        String msg = openAiClient.audioTranscriptions(file);
        WebResult webResult = WebResult.newSuccessInstance();
        webResult.putData("text", msg);
        return webResult;
    }
}
