package org.ligson.turing.vo.res;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Result {
    private int groupType;
    private String resultType;
    private ResultValue values;
}
