package org.ligson.ichat.photo.zuotang;

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
public class PhotoDownload {

    @Autowired
    private MyHttpClient myHttpClient;

    public String getImg(String imageUrl, String downloadDir) throws IOException {
        CloseableHttpClient httpClient = myHttpClient.getHttpClient();
        HttpGet httpGet = new HttpGet(imageUrl);
        String imageID = imageUrl.substring(0, imageUrl.lastIndexOf("?"));
        imageID = imageID.substring(imageID.lastIndexOf("/") + 1);
        File file = new File(downloadDir + imageID);
        httpClient.execute(httpGet, (response) -> {
            IOUtils.copyLarge(response.getEntity().getContent(), new FileOutputStream(file));
            return null;
        });
        return imageID;
    }
}
