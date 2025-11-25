package com.sakuray.repo;

import com.sakuray.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 文件数据访问接口 (File Data Access Repository)
 * 继承 JpaRepository 获得基本的 CRUD 和分页功能。
 */
@Repository
public interface FileRepo extends JpaRepository<File, Long> {

    // Spring Data JPA 自动实现 findByPath
    File findByPath(String path);
}