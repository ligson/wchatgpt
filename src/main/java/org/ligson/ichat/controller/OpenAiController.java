package org.ligson.ichat.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.openai.vo.req.ChatCompletionsReq;
import org.ligson.ichat.service.OpenAIChatServiceImpl;
import org.ligson.ichat.service.UserService;
import org.ligson.ichat.vo.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/openai")
@Slf4j
public class OpenAiController {
    @Autowired
    private OpenAIChatServiceImpl openAIChatService;
    @Autowired
    private UserService userService;
    private static Map<String, Date> userLastChat = new ConcurrentHashMap<>();

    @PostMapping("/chat")
    public WebResult handle(@RequestBody ChatCompletionsReq completionsReq,
                            HttpServletRequest request) throws IOException {
        long startTime = System.currentTimeMillis();
        WebResult webResult = new WebResult();
        String token = request.getHeader("token");
        //帮我生成一张沙滩的高清图片
        User user = userService.getLoginUserByToken(token);
        if (StringUtils.isBlank(token) || user == null) {
            webResult.setSuccess(false);
            webResult.setErrorMsg("您的登录会话已经过期，请重新登录!");
            return webResult;
        }
        Date date = userLastChat.get(user.getName());
        if (date != null) {
            if ((date.getTime() - startTime) / 1000.0 < 10) {
                webResult.setSuccess(false);
                webResult.setErrorMsg("接口性能有限，请稍候再试!");
                return webResult;
            }
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
