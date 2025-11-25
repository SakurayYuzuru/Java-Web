package com.sakuray.dto.file;

import jakarta.validation.constraints.Size;

/**
 * 文件更新请求 DTO (File Update Request DTO)
 * 仅包含可编辑的字段：文件名和描述。
 */
public class FileUpdateDTO {

    @Size(min = 1, max = 255, message = "文件名长度必须在1到255之间")
    private String name;

    @Size(max = 512, message = "描述信息长度不能超过512")
    private String description;

    // --- Getters and Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}