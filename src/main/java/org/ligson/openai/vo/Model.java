package org.ligson.openai.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Model {
    private String id;
    private String object;
    private long created;
    @JsonProperty("owned_by")
    private String ownedBy;

}
