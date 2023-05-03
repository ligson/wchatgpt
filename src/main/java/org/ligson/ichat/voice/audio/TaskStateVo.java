package org.ligson.ichat.voice.audio;

import lombok.Data;

@Data
public class TaskStateVo {
    private int code;
    private String fileconvertinfos;
    private int fileconvertsize;
    private int filesize;
    private String message;
    private String statusdetail;
    private int uploadtime;
}
