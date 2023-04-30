package org.ligson.ichat.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.ligson.ichat.service.CaptchaService;
import org.ligson.ichat.vo.WebResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Value("${app.openai.img-dir}")
    private String captchaPath;

    @Value("${app.server.domain-url}")
    private String domainUrl;

    /**
     * 缓存10张验证码
     * 正常1小时失效，我这里设置 50分钟失效
     */
    public ExpiringMap<Integer, LineCaptcha> imageCaptchaCache = ExpiringMap.builder()
            .maxSize(10)
            .expiration(50, TimeUnit.MINUTES)
            .variableExpiration().expirationPolicy(ExpirationPolicy.CREATED).build();


    @Override
    public WebResult generate() {
        int index = new Random().nextInt(10);
        LineCaptcha lineCaptcha = imageCaptchaCache.get(index);
        if (lineCaptcha == null) {
            synchronized (CaptchaServiceImpl.class) {
                LineCaptcha lineCaptcha2 = imageCaptchaCache.get(index);
                if (lineCaptcha2 == null) {
                    lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);
                    lineCaptcha.write(captchaPath + "user-images/captcha" + index + ".png");
                    imageCaptchaCache.put(index, lineCaptcha);
                }
            }
        }
        WebResult webResult = WebResult.newInstance();
        webResult.setSuccess(true);
        webResult.putData("captchaKey", index);
        webResult.putData("captchaPath", domainUrl + "/user-images/captcha" + index + ".png");
        return webResult;
    }

    @Override
    public WebResult verify(Integer captchaKey, String userInputCode) {
        LineCaptcha lineCaptcha = imageCaptchaCache.get(captchaKey);
        if (lineCaptcha == null) {
            return WebResult.newErrorInstance("验证码已经失效,请刷新重试！");
        }
        boolean verify = lineCaptcha.verify(userInputCode);
        WebResult webResult = WebResult.newInstance();
        webResult.setSuccess(verify);
        if (!verify) {
            webResult.setErrorMsg("验证码验证失败！");
        }
        return webResult;
    }
}
