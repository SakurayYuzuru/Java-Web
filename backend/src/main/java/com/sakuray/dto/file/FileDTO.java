package com.sakuray.dto.file;

import java.time.LocalDateTime;

import com.sakuray.entity.File;

/**
 * 文件响应DTO (File Response DTO)
 * 用于向前端展示文件列表或文件详情。
 */
public class FileDTO {

    private Long id;
    private String name;
    private String path; // 路径通常不应该直接暴露，但在测试环境中保留
    private String description;
    private Long size;
    private LocalDateTime uploadTime;

    public FileDTO() {
    }

    // 静态工厂方法，用于将 Entity 转换为 DTO
    public static FileDTO fromEntity(File file) {
        FileDTO dto = new FileDTO();
        dto.setId(file.getId());
        dto.setName(file.getName());
        dto.setPath(file.getPath());
        dto.setDescription(file.getDescription());
        dto.setSize(file.getSize());
        dto.setUploadTime(file.getUploadTime());
        return dto;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }
}