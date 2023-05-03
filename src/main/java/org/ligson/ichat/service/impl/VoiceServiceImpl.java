package org.ligson.ichat.service.impl;

import org.ligson.ichat.service.VoiceService;
import org.ligson.ichat.voice.audio.*;
import org.ligson.ichat.voice.text.*;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class VoiceServiceImpl implements VoiceService {

    @Override
    public String audioToText(String audioPath) {
        try {
            String filename = UUID.randomUUID().toString() + ".mp3";
            TokenVO tokenVO = TextUploadPar.uploadpar(filename);
            UploadVocTest.uploadFile(audioPath, tokenVO.getTasktag(), tokenVO.getTasktoken(), Integer.parseInt(tokenVO.getTimestamp()));
            TaskStateVo taskstate = TextTaskState.taskstate(tokenVO.getTasktag());
            while (taskstate.getCode() != 10000) {
                if (taskstate.getCode() == 18000) {
                    return taskstate.getMessage();
                }
                Thread.sleep(1000);
                taskstate = TextTaskState.taskstate(tokenVO.getTasktag());
            }
            TaskDownVo taskdown = TextTaskDown.taskdown(tokenVO.getTasktag());
            return TextDownload.download(taskdown.getDownurl());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String textToAudio(String text) {
        try {
            String filename = UUID.randomUUID().toString() + ".txt";
            TokenVO tokenVO = AudioUploadPar.uploadpar(filename);
            AudioUploadText.uploadtext(text, tokenVO.getTasktag(), tokenVO.getTasktoken(), Integer.parseInt(tokenVO.getTimestamp()));
            TaskStateVo taskstate = AudioTaskState.taskstate(tokenVO.getTasktag());
            while (taskstate.getCode() != 10000) {
                Thread.sleep(1000);
                taskstate = AudioTaskState.taskstate(tokenVO.getTasktag());
            }
            TaskDownVo taskdown = AudioTaskDown.taskdown(tokenVO.getTasktag());
            return taskdown.getDownurl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
