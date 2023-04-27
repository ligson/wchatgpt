package org.ligson.ichat.openai.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModelResult {
    private String object;
    private List<Model> data = new ArrayList<>();
}
