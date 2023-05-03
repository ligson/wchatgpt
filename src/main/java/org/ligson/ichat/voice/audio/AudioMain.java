package org.ligson.ichat.voice.audio;

import com.fasterxml.jackson.core.JsonProcessingException;

public class AudioMain {

    public static void main(String[] args) throws JsonProcessingException, InterruptedException {
        String filename = "在线版全能.txt";
        String textvoice = "在线版全能PDF转换助手文件仅限于100字以内的文件";

        TokenVO tokenVO = AudioUploadPar.uploadpar(filename);
        AudioUploadText.uploadtext(textvoice, tokenVO.getTasktag(), tokenVO.getTasktoken(), Integer.parseInt(tokenVO.getTimestamp()));
        TaskStateVo taskstate = AudioTaskState.taskstate(tokenVO.getTasktag());
        while (taskstate.getCode() != 10000) {
            Thread.sleep(1000);
            taskstate = AudioTaskState.taskstate(tokenVO.getTasktag());
        }
        TaskDownVo taskdown = AudioTaskDown.taskdown(tokenVO.getTasktag());
    }
}
