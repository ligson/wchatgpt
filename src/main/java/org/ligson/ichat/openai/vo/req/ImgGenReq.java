package org.ligson.ichat.openai.vo.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImgGenReq {
    private String prompt;
    //1-10
    private int n = 1;

    private String size = "1024x1024";
    @JsonProperty("context")
    private ReqContext context;
}
