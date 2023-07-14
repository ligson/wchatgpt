package org.ligson.ichat.fw.simplecrud.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QueryCondition {
    private boolean andRelation = true;
    private List<QueryField> queryFields = new ArrayList<>();
}
