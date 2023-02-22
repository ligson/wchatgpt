package org.ligson.turing.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TuringResult {
    private Intent intent;
    private List<Results> results = new ArrayList<>();

}
