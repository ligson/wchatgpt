package org.ligson.ichat.voice.text;

import lombok.Data;

@Data
public class TextContent {
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
    private String  fanyi_from;
    private String  fanyi_to;
    private String  pagerange;
    private String  productinfo;
    private String  deviceid;
    private int  timestamp;
    private String  datasign;
}
