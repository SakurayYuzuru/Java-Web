package com.sakuray.entity;

import jakarta.persistence.Column; // Spring Boot 3+ 使用 jakarta.persistence
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 标记这是一个 JPA 实体，对应数据库中的一张表
@Table(name = "user") // 指定表名，避免与数据库关键字冲突
public class User {
    
    @Id // 标记为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 设置主键生成策略（自增）
    private Long id; // 唯一的ID作为主键
    
    @Column(nullable = false, unique = true) // 约束：非空且唯一
    private String username;
    
    @Column(nullable = false)
    private String password; // 存储的是哈希后的密码

    // 默认构造函数是 JPA 规范必需的
    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters (ID 的 Getter/Setter 也是必需的)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}