package org.ligson.ichat.openai.vo.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Choice {
    private String text;
    private int index;
    private String logprobs;
    @JsonProperty("finish_reason")
    private String finishReason;

}
