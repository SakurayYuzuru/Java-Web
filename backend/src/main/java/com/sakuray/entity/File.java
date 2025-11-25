package com.sakuray.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 文件实体 (File Entity)
 * 映射到数据库中的 'file' 表。
 */
@Entity
@Table(name = "file")
public class File {

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增策略
    private Long id;

    @Column(nullable = false, length = 255) // NOT NULL, VARCHAR(255)
    private String name;

    @Column(nullable = false, unique = true, length = 255) // NOT NULL, UNIQUE, VARCHAR(255)
    private String path;

    @Column(length = 512) // VARCHAR(512)
    private String description;

    @Column(nullable = false) // NOT NULL, BIGINT 映射为 Java Long
    private Long size;

    @Column(name = "upload_time", nullable = false) // NOT NULL, DATETIME 映射为 LocalDateTime
    private LocalDateTime uploadTime;

    // --- 构造函数 (Constructors) ---

    // JPA 规范要求必须提供无参构造函数
    public File() {
    }

    // 常用构造函数 (不包含自增的 id)
    public File(String name, String path, String description, Long size, LocalDateTime uploadTime) {
        this.name = name;
        this.path = path;
        this.description = description;
        this.size = size;
        this.uploadTime = uploadTime;
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