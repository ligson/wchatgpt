package org.ligson.ichat.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.ligson.ichat.domain.User;
import org.ligson.ichat.openai.vo.req.ChatCompletionsReq;
import org.ligson.ichat.openai.vo.req.Message;
import org.ligson.ichat.service.impl.OpenAIChatServiceImpl;
import org.ligson.ichat.service.UserService;
import org.ligson.ichat.vo.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
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
        if (user.getTimes() <= 0) {
            webResult.setSuccess(false);
            webResult.setErrorMsg("您的使用次数已经用完，请联系管理员续费!");
            return webResult;
        }
        user.setTimes(user.getTimes() - 1);

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

    @PostMapping("/img")
    public WebResult img(@RequestBody ChatCompletionsReq completionsReq,
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
        if (user.getTimes() <= 0) {
            webResult.setSuccess(false);
            webResult.setErrorMsg("您的使用次数已经用完，请联系管理员续费!");
            return webResult;
        }
        user.setTimes(user.getTimes() - 1);
        Message message = completionsReq.getMessages().get(completionsReq.getMessages().size() - 1);
        String msg = openAIChatService.img(null, message.getContent());
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

    @PostMapping("/audio2txt")
    public WebResult audio2Txt(@RequestPart("file") MultipartFile multipartFile) throws IOException {
        String orginFileName = multipartFile.getOriginalFilename();
        String prefix = FilenameUtils.getPrefix(orginFileName);
        String ext = FilenameUtils.getExtension(orginFileName);
        if("blob".equals(orginFileName)){
            prefix = UUID.randomUUID().toString();
        }
        if (StringUtils.isBlank(prefix)) {
            return WebResult.newErrorInstance("参数错误");
        }
        if (StringUtils.isBlank(ext) && multipartFile.getContentType() != null) {
            if ("audio/wav".equals(multipartFile.getContentType())) {
                ext = ".wav";
            }
        }
        if (StringUtils.isBlank(ext)) {
            return WebResult.newErrorInstance("格式不支持");
        }
        File file = File.createTempFile(prefix, ext);
        multipartFile.transferTo(file);
        return openAIChatService.audio2txt(file);
    }
}
