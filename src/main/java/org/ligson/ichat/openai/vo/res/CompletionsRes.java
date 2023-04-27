package org.ligson.ichat.openai.vo.res;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CompletionsRes {
    private String id;
    private String object;
    private Date created;
    private String model;

    private List<Choice> choices = new ArrayList<>();
    private Usage usage;
}
