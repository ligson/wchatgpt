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
import org.ligson.vo.AppConfig;
import org.ligson.vo.RegisterDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@BootService
public class UpgradeHandler implements HttpHandler {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9]{6,12}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-z0-9_]{8,}$");


    @BootAutowired
    private CruxSerializer cruxSerializer;

    @BootAutowired
    private AppConfig appConfig;

    @BootAutowired
    private HttpServerResponseConverter httpServerResponseConverter;
    @BootAutowired
    private ServerUserContext serverUserContext;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, Object> result = new HashMap<>();
        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            RegisterDTO req = cruxSerializer.deserialize(IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8), RegisterDTO.class);
            if (StringUtils.isNotBlank(req.getUsername()) && StringUtils.isNotBlank(req.getRegisterCode())) {
                String registerCode = appConfig.getApp().getServer().getRegisterCode();
                if (!registerCode.equals(req.getRegisterCode())) {
                    result.put("success", false);
                    result.put("msg", "注册码错误!");
                    httpServerResponseConverter.processResult(result, exchange);
                    return;
                }

                Matcher usernameMatcher = USERNAME_PATTERN.matcher(req.getUsername());
                if (!usernameMatcher.matches()) {
                    result.put("success", false);
                    result.put("msg", "账号格式错误!");
                    httpServerResponseConverter.processResult(result, exchange);
                    return;
                }

                result = serverUserContext.upgrade(req.getUsername());
                httpServerResponseConverter.processResult(result, exchange);
                return;
            } else {
                result.put("success", false);
                result.put("msg", "参数格式错误!");
                httpServerResponseConverter.processResult(result, exchange);
                return;
            }
        }
        result.put("success", false);
        result.put("msg", "格式错误!");
        httpServerResponseConverter.processResult(result, exchange);
    }
}
