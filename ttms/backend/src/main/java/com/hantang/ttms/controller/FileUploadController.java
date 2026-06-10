package com.hantang.ttms.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.dto.AdminApiResponse;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/admin/api")
public class FileUploadController {

    private final Path uploadRoot;

    public FileUploadController(@Value("${ttms.upload.path:/var/www/html/theater_ticket/images}") String uploadPath) {
        this.uploadRoot = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录: " + uploadRoot, e);
        }
    }

    /** 上传图片 */
    @PostMapping("/upload")
    public AdminApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件为空");
        }

        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("仅支持上传图片文件");
        }

        // 校验文件大小（最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("图片大小不能超过5MB");
        }

        // 生成文件名：日期/原始扩展名
        String originalName = file.getOriginalFilename();
        String ext = ".jpg";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString().substring(0, 8) + ext;
        String datePath = LocalDate.now().toString();
        Path targetDir = uploadRoot.resolve(datePath);
        try {
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(filename);
            file.transferTo(targetFile.toFile());
        } catch (IOException e) {
            throw new BusinessException("文件保存失败: " + e.getMessage());
        }

        String url = "/images/" + datePath + "/" + filename;
        return AdminApiResponse.ok(Map.of("url", url));
    }
}
