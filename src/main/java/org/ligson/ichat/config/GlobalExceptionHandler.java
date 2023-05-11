package org.ligson.ichat.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.ligson.ichat.enums.ErrorType;
import org.ligson.ichat.ex.BussinessException;
import org.ligson.ichat.ex.InnerException;
import org.ligson.ichat.vo.WebResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = InnerException.class)
    public WebResult businessExceptionHandler(InnerException e, HttpServletResponse response) {
        WebResult webResult = WebResult.newErrorInstance(e.getMessage());
        webResult.setErrorType(ErrorType.Inner);
        webResult.setStackTrace(ExceptionUtils.getStackTrace(e));
        webResult.setHttpCode(500);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        log.error("内部错误:{},stack:{}", e.getMessage(), webResult.getStackTrace());
        return webResult;
    }

    @ResponseBody
    @ExceptionHandler(value = BussinessException.class)
    public WebResult innerExceptionHandler(BussinessException e, HttpServletResponse response) {
        WebResult webResult = WebResult.newErrorInstance(e.getMessage());
        webResult.setErrorType(ErrorType.Business);
        webResult.setStackTrace(ExceptionUtils.getStackTrace(e));
        webResult.setHttpCode(500);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        log.error("业务错误:{},stack:{}", e.getMessage(), webResult.getStackTrace());
        return webResult;
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public WebResult exceptionHandler(Exception e, HttpServletResponse response) {
        WebResult webResult = WebResult.newErrorInstance(e.getMessage());
        webResult.setErrorType(ErrorType.Inner);
        webResult.setStackTrace(ExceptionUtils.getStackTrace(e));
        webResult.setHttpCode(500);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        log.error("错误:{},stack:{}", e.getMessage(), webResult.getStackTrace());
        return webResult;
    }
}
