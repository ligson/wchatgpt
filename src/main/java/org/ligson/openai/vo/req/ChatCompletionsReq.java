package org.ligson.openai.vo.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatCompletionsReq {
    private String model = "gpt-3.5-turbo";
    @JsonProperty("max_tokens")
    private int maxTokens = 2000;
    private String prompt;
    @JsonProperty("top_p")
    private int topP = 1;
    //控制结果随机性，0.0表示结果固定，随机性大可以设置为0.9
    private double temperature = 0.9;

    @JsonProperty("frequency_penalty")
    private int frequencyPenalty;
    @JsonProperty("presence_penalty")
    private int presencePenalty;

    private List<Message> messages = new ArrayList<>();
}
