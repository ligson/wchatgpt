package org.ligson.turing.vo.res;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TuringResult {
    private Intent intent;
    private List<Result> results = new ArrayList<>();

}
