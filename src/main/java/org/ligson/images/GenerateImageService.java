package org.ligson.images;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class GenerateImageService {

    /**
     * 请求token缓存
     * 正常1小时失效，我这里设置 50分钟失效
     */
    public ExpiringMap<String, String> imageTokenCache = ExpiringMap.builder()
            .maxSize(10)
            .expiration(50, TimeUnit.MINUTES)
            .variableExpiration().expirationPolicy(ExpirationPolicy.CREATED).build();

    /**
     * 生成图片
     *
     * @param prompt 文本
     * @return imageUrl
     */
    public String imageGenerate(String prompt) {
        prompt = prompt.replace("图片", "");
        try {
            return doImageGenerate(prompt);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getToken() {
        RestTemplate restTemplate = new RestTemplate();
        String refresh_token = "APJWN8dJQbLJ_VyfE4_UoYEkmJO3f8cRKiB4UARK24Hlop1Ene0e32pB4vkQoJ8_LvevEiUjJxdulR1MUO4CQBljuyoTQZhTLnA9Ju9jlBt3b-FZP9-bTvRWOjpoOgEGv3lj90PXrsv_lX6X3LX_2YhFImkupz-mmVwCvPzprEkvVK5_YOqA6RE";
        String key = "AIzaSyDCvp5MTJLUdtBYEKYWXJrlLzu1zuKM6Xw&";
        String tokenUrl = "https://securetoken.googleapis.com/v1/token?key=" + key;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("grant_type", Collections.singletonList("refresh_token"));
        params.put("refresh_token", Collections.singletonList(refresh_token));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<TokenInfo> res = restTemplate.exchange(tokenUrl, HttpMethod.POST, httpEntity, TokenInfo.class);
        TokenInfo body = res.getBody();
        assert body != null;
        imageTokenCache.put("token", body.getAccess_token());
        return body.getAccess_token();
    }

    private String doImageGenerate(String prompt) {
        String url = "https://paint.api.wombo.ai/api/v2/tasks";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        String token = Optional.ofNullable(imageTokenCache.get("token")).orElse(getToken());
        headers.add("Authorization", "Bearer " + token);
        PromptInfo promptInfo = new PromptInfo();
        promptInfo.setPrompt(prompt);
        promptInfo.setStyle(78);
        promptInfo.setDisplay_freq(10);
        DrawParam drawParam = new DrawParam();
        drawParam.set_premium(false);
        drawParam.setInput_spec(promptInfo);

        HttpEntity<?> httpEntity = new HttpEntity<>(drawParam, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<DrawResult> res = restTemplate.exchange(url, HttpMethod.POST, httpEntity, DrawResult.class);
        DrawResult body = res.getBody();
        assert body != null;
        String imageId = body.getId();
        if ("pending".equals(body.getState())) {
            for (int i = 0; i < 30; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DrawResult result = queryImageStatus(imageId);
                if (result != null && "completed".equals(result.getState())) {
                    return result.getResult().getFinalValue();
                }
            }
        }
        return body.getResult().getFinalValue();
    }

    private DrawResult queryImageStatus(String imageId) {
        String url = "https://paint.api.wombo.ai/api/v2/tasks/" + imageId;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<DrawResult> res = restTemplate.exchange(url, HttpMethod.GET, httpEntity, DrawResult.class);
        return res.getBody();
    }

    public static void main(String[] args) {
        String imageUrl = new GenerateImageService().imageGenerate("画一只小鸟");
        System.out.println(imageUrl);
    }
}
