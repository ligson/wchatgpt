package org.ligson.openai.vo.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.ligson.openai.vo.req.Message;

@Data
public class ChatChoice {
    private int index;
    @JsonProperty("finish_reason")
    private String finishReason;
    private Message message;

}
