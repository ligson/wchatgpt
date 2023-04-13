package org.ligson.openai.vo.req;

import lombok.Data;

@Data
public class Message {
    private String role;
    private String content;
}
