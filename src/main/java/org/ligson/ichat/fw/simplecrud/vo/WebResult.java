package org.ligson.ichat.fw.simplecrud.vo;

import lombok.Data;
import org.ligson.ichat.fw.enums.ErrorType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class WebResult {
    private boolean success;
    private int httpCode = 200;
    private String errorMsg;
    private String stackTrace;
    private ErrorType errorType;
    private Map<String, Object> data = new ConcurrentHashMap<>();

    public static WebResult newInstance() {
        return new WebResult();
    }

    public static WebResult newSuccessInstance() {
        WebResult result = new WebResult();
        result.setSuccess(true);
        return result;
    }

    public static WebResult newErrorInstance(String errorMsg) {
        WebResult result = new WebResult();
        result.setSuccess(false);
        result.setErrorMsg(errorMsg);
        return result;
    }

    public WebResult putData(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public void setErrorMsg(String errorMsg) {
        success = false;
        this.errorMsg = errorMsg;
    }

}
