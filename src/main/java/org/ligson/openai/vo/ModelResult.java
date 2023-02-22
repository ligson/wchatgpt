package org.ligson.openai.vo;

import lombok.Data;
import org.ligson.openai.vo.Model;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModelResult {
    private String object;
    private List<Model> data = new ArrayList<>();
}
