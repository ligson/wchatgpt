package org.ligson.ichat.service;

import org.ligson.ichat.vo.WebResult;

public interface CaptchaService {

    WebResult generate();

    WebResult verify(Integer captchaKey, String userInputCode);
}
