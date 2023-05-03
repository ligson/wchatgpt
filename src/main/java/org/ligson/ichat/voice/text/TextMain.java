package org.ligson.ichat.voice.text;

import org.ligson.ichat.voice.audio.*;

import java.io.IOException;

public class TextMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        String filename = "在线版全能_315072.mp3";
        String filePath = "/Users/jinmancang1/Downloads/在线版全能_31507.mp3";
        TokenVO tokenVO = TextUploadPar.uploadpar(filename);
        UploadVocTest.uploadFile(filePath, tokenVO.getTasktag(), tokenVO.getTasktoken(), Integer.parseInt(tokenVO.getTimestamp()));
        TaskStateVo taskstate = TextTaskState.taskstate(tokenVO.getTasktag());
        while (taskstate.getCode() != 10000) {
            Thread.sleep(1000);
            taskstate = TextTaskState.taskstate(tokenVO.getTasktag());
        }
        TaskDownVo taskdown = TextTaskDown.taskdown(tokenVO.getTasktag());
        TextDownload.download(taskdown.getDownurl());
    }
}
