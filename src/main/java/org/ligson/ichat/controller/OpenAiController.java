package org.ligson.ichat.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ligson.ichat.openai.vo.req.ChatCompletionsReq;
import org.ligson.ichat.service.OpenAIChatServiceImpl;
import org.ligson.ichat.service.UserService;
import org.ligson.ichat.vo.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/openai")
@Slf4j
public class OpenAiController {
    @Autowired
    private OpenAIChatServiceImpl openAIChatService;
    @Autowired
    private UserService userService;

    @PostMapping("/chat")
    public WebResult handle(@RequestBody ChatCompletionsReq completionsReq,
                            HttpServletRequest request) throws IOException {
        long startTime = System.currentTimeMillis();
        WebResult webResult = new WebResult();
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token) || userService.getLoginUserByToken(token) == null) {
            webResult.setSuccess(false);
            webResult.setErrorMsg("您的登录会话已经过期，请重新登录!");
            return webResult;
        }
        String msg = openAIChatService.chat(completionsReq);
        if (msg != null) {
            webResult.setSuccess(true);
            webResult.putData("msg", msg);
        } else {
            webResult.setSuccess(false);
            webResult.setErrorMsg("未知错误,请重试");

        }
        long endTime = System.currentTimeMillis();
        double total = (endTime - startTime) / 1000.0;
        log.debug("调用openai接口耗时:{}s", total);
        webResult.putData("totalTime", total);
        return webResult;
    }

    @GetMapping("/models")
    public WebResult models() {
        return WebResult.newSuccessInstance().putData("models", openAIChatService.models());
    }
}
