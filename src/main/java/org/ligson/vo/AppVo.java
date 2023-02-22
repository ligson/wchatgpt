package org.ligson.vo;

import lombok.Data;

@Data
public class AppVo {
    private TuringConfig turing;
    private OpenAIConfig openai;
    private ServerVo server;
}
