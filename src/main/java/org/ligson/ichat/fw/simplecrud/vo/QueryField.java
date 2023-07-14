package org.ligson.ichat.fw.simplecrud.vo;

import lombok.Data;
import org.ligson.ichat.fw.simplecrud.operator.QueryOperator;

@Data
public class QueryField {
    private String name;
    private QueryOperator operator;
    private Object value;
}
