package org.ligson.turing.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Results {
    private int groupType;
    private String resultType;
    private List<ResultValue> values = new ArrayList<>();
}
