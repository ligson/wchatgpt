package org.ligson.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.ligson.fw.annotation.BootAutowired;
import org.ligson.fw.annotation.BootService;
import org.ligson.http.HttpServerResponseConverter;
import org.ligson.http.ServerUserContext;
import org.ligson.serializer.CruxSerializer;
import org.ligson.vo.ResetPwdDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@BootService
public class ResetPwdHandler implements HttpHandler {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9]{6,12}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-z0-9_]{8,}$");


    @BootAutowired
    private CruxSerializer cruxSerializer;

    @BootAutowired
    private HttpServerResponseConverter httpServerResponseConverter;
    @BootAutowired
    private ServerUserContext serverUserContext;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            ResetPwdDTO req = cruxSerializer.deserialize(IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8), ResetPwdDTO.class);
            if (StringUtils.isNotBlank(req.getUsername()) && StringUtils.isNotBlank(req.getOldPassword()) && StringUtils.isNotBlank(req.getNewPassword())) {

                Matcher passwordMatcher = PASSWORD_PATTERN.matcher(req.getNewPassword());
                if (!passwordMatcher.matches()) {
                    result.put("success", false);
                    result.put("msg", "密码格式错误!");
                    httpServerResponseConverter.processResult(result, exchange);
                    return;
                }
                result = serverUserContext.resetPassword(req.getUsername(), req.getOldPassword(), req.getNewPassword());
                httpServerResponseConverter.processResult(result, exchange);
                return;
            } else {
                result.put("success", false);
                result.put("msg", "参数格式错误!");
                return;
            }
        }
        result.put("success", false);
        result.put("msg", "格式错误!");
        httpServerResponseConverter.processResult(result, exchange);
    }
}
