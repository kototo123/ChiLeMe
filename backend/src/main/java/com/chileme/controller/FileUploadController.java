package com.chileme.controller;

import com.chileme.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Value("${chileme.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        Path uploadPath = Paths.get(uploadDir, "avatars");
        Files.createDirectories(uploadPath);

        String ext = file.getOriginalFilename();
        ext = ext != null && ext.contains(".") ? ext.substring(ext.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID().toString() + ext;

        Path dest = uploadPath.resolve(filename);
        file.transferTo(dest.toFile());

        String url = "/uploads/avatars/" + filename;
        return Result.success(url);
    }
}
