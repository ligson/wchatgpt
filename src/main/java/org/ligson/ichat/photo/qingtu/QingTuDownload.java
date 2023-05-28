package org.ligson.ichat.photo.qingtu;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.ligson.ichat.util.MyHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class QingTuDownload {

    @Autowired
    private MyHttpClient myHttpClient;

    public String getImg(String imageID, String downloadDir) throws IOException {
        CloseableHttpClient httpClient = myHttpClient.getHttpClient();
        String url = "https://qingtu.cn/show/result/" + imageID;
        HttpGet httpGet = new HttpGet(url);
        File file = new File(downloadDir + imageID + ".jpg");
        httpClient.execute(httpGet, (response) -> {
            IOUtils.copyLarge(response.getEntity().getContent(), new FileOutputStream(file));
            return null;
        });
        return imageID + ".jpg";
    }
}
