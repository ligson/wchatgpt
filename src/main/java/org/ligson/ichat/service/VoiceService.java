package org.ligson.ichat.service;

/**
 * 语音服务
 */
public interface VoiceService {

    /**
     * 语音转文本
     *
     * @param audioPath 语音路径
     * @return 文本
     */
    String audioToText(String audioPath);

    /**
     * 文本转语音
     *
     * @param text 文本
     * @return 语音地址
     */
    String textToAudio(String text);
}
