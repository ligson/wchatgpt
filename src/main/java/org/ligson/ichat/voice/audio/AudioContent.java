package org.ligson.ichat.voice.audio;

import lombok.Data;

@Data
public class AudioContent {
    private String filename;
    private int filecount;
    private String softname;
    private String softversion;
    private String machineid;
    private int productid;
    private String tasktype;
    private int limitsize;
    private int isshare;
    private String outputfileextension;
    private String parainfo;
    private int timestamp;
    private String productinfo;
    private String deviceid;
    private String datasign;
}