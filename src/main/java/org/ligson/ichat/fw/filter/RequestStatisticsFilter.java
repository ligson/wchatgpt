package org.ligson.ichat.fw.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Slf4j
@Component
@Order(FilterOrder.SERVLET_FILTER_REQUEST_COUNTER_ORDER)
public class RequestStatisticsFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String requestURI = request.getRequestURI();
        log.debug("开始请求:{},请求方法:{},请求参数:{}", requestURI, request.getMethod(), request.getQueryString());
        try {
            filterChain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            long countTime = endTime - startTime;
            log.debug("结束请求:{},状态:{},耗时:{}ms", requestURI, response.getStatus(), countTime);
            if (response.getStatus() != 200) {
                log.error("请求:{}返回状态不是200", requestURI);
            }
            log.debug("请求:{}记录日志成功", requestURI);
        }
    }
}
