package org.ligson.ichat.voice.audio;

import lombok.Data;

@Data
public class AudioContent2 {
//     "deviceid=43d8dec164e64699ba3208960ff5cc5c" +
//             "&fileindex=0" +
//             "&productinfo=04D83019EE0348F9B439414D9CF0809BF0DDB8F032DFD0B9120BF5D98C12B110AAFAEA1BC0CC792D" +
//             "&tasktag=924894887f44445da7ce678f497c4196" +
//             "&tasktoken=672ecd01f1bd94775068375e4b42036e" +
//             "&textvoice=在线版全能PDF转换助手文件仅限于100字以内的文件，如需转换更大文" +
//             "&timestamp=1683013526hUuPd20171206LuOnD"

    private String deviceid;
    private int fileindex;
    private String productinfo;
    private String tasktag;
    private String tasktoken;
    private String textvoice;
    private int timestamp;
    private String datasign;
}