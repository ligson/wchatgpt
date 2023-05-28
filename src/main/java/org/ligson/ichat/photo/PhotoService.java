package org.ligson.ichat.photo;

import org.ligson.ichat.photo.qingtu.QingTuDownload;
import org.ligson.ichat.photo.qingtu.QingTuToken;
import org.ligson.ichat.photo.qingtu.QingTuUpload;
import org.ligson.ichat.photo.zuotang.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@Service
public class PhotoService {

    @Autowired
    private PhotoLogin photoLogin;
    @Autowired
    private PhotoOss photoOss;
    @Autowired
    private PhotoUpload photoUpload;
    @Autowired
    private PhotoScale photoScale;
    @Autowired
    private PhotoScaleGet photoScaleGet;
    @Autowired
    private PhotoDownload photoDownload;

    @Autowired
    private QingTuUpload qingTuUpload;
    @Autowired
    private QingTuToken qingTuToken;
    @Autowired
    private QingTuDownload qingTuDownload;

    @Value("${app.openai.img-dir}")
    private String imgDir;
    @Value("${app.server.domain-url}")
    private String domainUrl;

    public String qingTu(String imageUrl) throws Exception {
        Map<String, String> map = qingTuToken.getToken();
        String cookie = map.get("cookie");
        String xCsrfToken = map.get("X-CSRF-TOKEN");
        Map<String, String> res = qingTuUpload.upload(imageUrl, cookie, xCsrfToken);
        String imageID = res.get("hashid");
        Thread.sleep(3000);
        String downloadDir = imgDir + "user-images/";
        String img = qingTuDownload.getImg(imageID, downloadDir);
        return domainUrl + "/user-images/" + img;
    }

    public String zuoTang(String localImgPath) throws Exception {
        //登录接口
        Map<String, String> resMap = photoLogin.login();
        String deviceId = resMap.get("deviceId");
        String apiToken = resMap.get("apiToken");

        //OSS接口
        String imageName = localImgPath.substring(localImgPath.lastIndexOf("/") + 1);
        Map<String, String> oss = photoOss.oss(apiToken, imageName);
        String accessKeyId = oss.get("accessKeyId");
        String accessKeySecret = oss.get("accessKeySecret");
        String bucket = oss.get("bucket");
        String objectName = oss.get("objectName");
        String securityToken = oss.get("securityToken");
        String callback = encryptCallback(deviceId);
        System.out.println(callback);
        String date = toUTCString();
        String part = AuthorizationPart.getPart(accessKeySecret, bucket, objectName, securityToken, date, callback);
        String authorization = "OSS " + accessKeyId + ":" + part;
        System.out.println(authorization);

        //upload接口
        Map<String, String> upload = photoUpload.upload(authorization, objectName, callback, date, securityToken, localImgPath);
        String resourceId = upload.get("resourceId");

        //获取用户权限
        Map<String, String> scale = photoScale.scale(apiToken, resourceId);
        String taskId = scale.get("taskId");

        //获取imgUrl
        Map<String, String> imgInfo = photoScaleGet.getImgUrl(apiToken, taskId);
        String progress = imgInfo.get("progress");
        int times = 5;
        while (!progress.equals("100")) {
            Thread.sleep(1000);
            imgInfo = photoScaleGet.getImgUrl(apiToken, taskId);
            progress = imgInfo.get("progress");
            if (times-- <= 0) {
                break;
            }
        }
        String imageUrl = imgInfo.get("image");
        String downloadDir = imgDir + "user-images/";
        String img = photoDownload.getImg(imageUrl, downloadDir);
        return domainUrl + "/user-images/" + img;
    }

    private static String toUTCString() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return (sdf.format(new Date()));
    }

    private static String encryptCallback(String userId) {
        String e = "{\"callbackUrl\":\"https://aw.aoscdn.com/app/picwish/callbacks/aliyun/oss\",\"callbackBody\":\"bucket=${bucket}&object=${object}&size=${size}&mimeType=${mimeType}&imageInfo.height=${imageInfo.height}&imageInfo.width=${imageInfo.width}&imageInfo.format=${imageInfo.format}&x:filename=${filename}&x:user_id=" + userId + "\",\"callbackBodyType\":\"application/x-www-form-urlencoded\"}";
        String cipher = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        int t = 0, r = 0, a = 0, o = 0;
        StringBuilder s = new StringBuilder();
        int i = e.length() % 3;
        int n = 0;
        while (n < e.length()) {
            try {
                if (!((r = Character.codePointAt(e, n++)) > 255)) {
                    try {
                        if (!((a = Character.codePointAt(e, n++)) > 255)) {
                            try {
                                if ((o = Character.codePointAt(e, n++)) > 255) {
                                    throw new NullPointerException("Failed to execute 'btoa' on 'Window': The string to be encoded contains characters outside of the Latin1 range.");
                                }
                            } catch (Exception e3) {
                                o = 0;
                            }
                        }
                    } catch (Exception e2) {
                        a = 0;
                        o = 0;
                    }
                }
            } catch (Exception e1) {
                a = 0;
                o = 0;
            }
            t = r << 16 | a << 8 | o;
            s.append(cipher.charAt(t >> 18 & 63)).append(cipher.charAt(t >> 12 & 63)).append(cipher.charAt(t >> 6 & 63)).append(cipher.charAt(t & 63));
        }
        System.out.println(s);
        return i > 0 ? s.substring(0, s.length() - 3 + i) + "===".substring(i) : s.toString();
    }
}