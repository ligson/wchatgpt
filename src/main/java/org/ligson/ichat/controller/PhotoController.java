package org.ligson.ichat.controller;

import org.apache.commons.io.IOUtils;
import org.ligson.ichat.photo.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@CrossOrigin
@Controller
@RequestMapping("/api/auth")
public class PhotoController {

    @Value("${app.openai.img-dir}")
    private String imgDir;

    @Autowired
    private PhotoService photoService;

    @ResponseBody
    @PostMapping("/uploadPhoto")
    public String uploadPhoto(HttpServletRequest request) {
        MultipartFile multipartFile = ((MultipartHttpServletRequest) request).getFileMap().get("avatar");
        String filename = multipartFile.getOriginalFilename();
        try {
            File newFile = new File(imgDir + "user-images/" + filename);
            multipartFile.transferTo(newFile);
            boolean watermark = true;
            if (watermark) {
                return photoService.qingTu(newFile.getAbsolutePath());
            } else {
                return photoService.zuoTang(newFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "上传失败";
    }

    @GetMapping("/downloadChess")
    public void downloadChess(HttpServletResponse response) throws Exception {
        String fileName = "aichess.apk";
        String path = imgDir + "user-images/" + fileName;
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        response.setHeader("Connection", "close");
        response.setHeader("Content-Type", "application/octet-stream");
        IOUtils.copy(new FileInputStream(path), response.getOutputStream(), 8192);
    }

}
