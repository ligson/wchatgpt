package org.ligson.ichat.fw.simplecrud.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PageWebResult<T> extends WebResult {
    @JsonIgnore
    private List<T> datas = new ArrayList<>();
    @JsonIgnore
    private long total;

    public void setDatas(List<T> datas) {
        this.datas = datas;
        putData("datas", datas);
    }

    public void setTotal(long total) {
        this.total = total;
        putData("total", total);
    }

    public static <T> PageWebResult<T> newInstance(List<T> datas, long total) {
        PageWebResult<T> pageWebResult = new PageWebResult<>();
        pageWebResult.setSuccess(true);
        pageWebResult.setDatas(datas);
        pageWebResult.setTotal(total);
        return pageWebResult;
    }
}
