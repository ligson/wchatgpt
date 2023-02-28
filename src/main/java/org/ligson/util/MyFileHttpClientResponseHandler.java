package org.ligson.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
public class MyFileHttpClientResponseHandler implements HttpClientResponseHandler<File> {
    @Override
    public File handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
        if (response.getCode() == HttpStatus.SC_OK) {
            String fileName = UUID.randomUUID().toString();
            File tmpFile = File.createTempFile(fileName, ".jpg");
            FileOutputStream fos = new FileOutputStream(tmpFile);
            response.getEntity().writeTo(fos);
            fos.close();
            EntityUtils.consume(response.getEntity());
            Path path = tmpFile.toPath();
            String mimeType = Files.probeContentType(path);
            if (mimeType != null) {
                String[] arr = mimeType.split("/");
                if (arr.length == 2) {
                    File destFile = File.createTempFile(fileName, "." + arr[1]);
                    boolean re = tmpFile.renameTo(destFile);
                    if (re) {
                        return destFile;
                    } else {
                        log.warn("临时文件:{}重命名:{}失败", tmpFile.getAbsolutePath(), destFile.getAbsolutePath());
                        return tmpFile;
                    }
                } else {
                    log.warn("mime type类型不对：{}", mimeType);
                    return tmpFile;
                }
            } else {
                log.warn("mime type获取失败");
                return tmpFile;
            }
        }
        return null;
    }
}
