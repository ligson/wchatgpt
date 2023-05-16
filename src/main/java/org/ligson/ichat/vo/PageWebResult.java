package org.ligson.ichat.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PageWebResult<T> extends WebResult {
    @JsonIgnore
    private List<T> datas = new ArrayList<>();
    @JsonIgnore
    private int total;

    public void setDatas(List<T> datas) {
        this.datas = datas;
        putData("datas", datas);
    }

    public void setTotal(int total) {
        this.total = total;
        putData("total", total);
    }

    public static <T> PageWebResult<T> newInstance(List<T> datas, int total) {
        PageWebResult<T> pageWebResult = new PageWebResult<>();
        pageWebResult.setSuccess(true);
        pageWebResult.setDatas(datas);
        pageWebResult.setTotal(total);
        return pageWebResult;
    }
}
