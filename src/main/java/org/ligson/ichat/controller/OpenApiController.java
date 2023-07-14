package org.ligson.ichat.controller;


import lombok.extern.slf4j.Slf4j;
import org.ligson.ichat.openai.vo.req.ChatCompletionsReq;
import org.ligson.ichat.openai.vo.req.Message;
import org.ligson.ichat.service.VoiceService;
import org.ligson.ichat.service.impl.OpenAIChatServiceImpl;
import org.ligson.ichat.fw.simplecrud.vo.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/openapi")
public class OpenApiController {

    @Value("${app.openai.img-dir}")
    private String voiceDir;

    @Autowired
    private VoiceService voiceService;

    @Autowired
    private OpenAIChatServiceImpl openAIChatService;

    @PostMapping("/chat")
    public WebResult chat(@RequestBody ChatCompletionsReq completionsReq) {
        String msg = openAIChatService.chat(completionsReq);
        return WebResult.newSuccessInstance().putData("msg", msg);
    }

    @PostMapping("/img")
    public WebResult img(@RequestBody ChatCompletionsReq completionsReq) {
        Message message = completionsReq.getMessages().get(completionsReq.getMessages().size() - 1);
        String msg = openAIChatService.generateImgReturnUrl(message.getContent());
        return WebResult.newSuccessInstance().putData("msg", msg);
    }

    @PostMapping("/audioToText")
    public WebResult audioToText(@RequestPart("file") MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        try {
            File newFile = new File(voiceDir + filename);
            multipartFile.transferTo(newFile);
            String text = voiceService.audioToText(voiceDir + filename);
            return WebResult.newSuccessInstance().putData("msg", text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WebResult.newErrorInstance("上传失败");
    }

    @PostMapping("/textToAudio")
    public WebResult textToAudio(@RequestParam String msg) {
        String text = voiceService.textToAudio(msg);
        return WebResult.newSuccessInstance().putData("msg", text);
    }

}
