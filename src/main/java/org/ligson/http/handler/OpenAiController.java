package org.ligson.http.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.ligson.chat.impl.OpenAIChatServiceImpl;
import org.ligson.http.ServerUserContext;
import org.ligson.openai.vo.req.ChatCompletionsReq;
import org.ligson.vo.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/openai")
public class OpenAiController {
    @Autowired
    private OpenAIChatServiceImpl openAIChatService;
    @Autowired
    private ServerUserContext serverUserContext;


    @PostMapping("/chat")
    public WebResult handle(@RequestBody ChatCompletionsReq completionsReq,
                            HttpServletRequest request) throws IOException {
        WebResult webResult = new WebResult();
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token) || serverUserContext.getLoginUserByToken(token) == null) {
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
            webResult.setErrorMsg("未知错误");

        }
        return webResult;
    }

    @GetMapping("/models")
    public void models() {
        WebResult.newSuccessInstance().putData("models", openAIChatService.models());
    }
}
