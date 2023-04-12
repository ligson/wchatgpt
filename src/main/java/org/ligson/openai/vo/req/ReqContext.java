package org.ligson.openai.vo.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReqContext {
    @JsonProperty("conversation_id")
    private String conversationId;
}
