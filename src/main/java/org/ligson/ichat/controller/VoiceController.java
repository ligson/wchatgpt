package org.ligson.ichat.controller;

import lombok.extern.slf4j.Slf4j;
import org.ligson.ichat.service.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class VoiceController {

    @Value("${app.openai.img-dir}")
    private String voiceDir;

    @Autowired
    private VoiceService voiceService;

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestPart("upfile") MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        try {
            File newFile = new File(voiceDir + filename);
            multipartFile.transferTo(newFile);
            String text = voiceService.audioToText(voiceDir + filename);
            return voiceService.textToAudio(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传失败";
    }
}
