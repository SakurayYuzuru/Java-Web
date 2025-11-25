package com.sakuray.service;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.sakuray.dto.file.FileDTO;
import com.sakuray.dto.file.FileUpdateDTO;
import com.sakuray.entity.File;

/**
 * 文件业务服务接口 (File Business Service Interface)
 */
public interface FileService {

    /**
     * 上传文件并保存元数据
     * @param multipartFile 实际文件数据
     * @param description 文件描述
     * @return 保存后的文件实体
     * @throws IOException 文件操作异常
     */
    FileDTO uploadFile(MultipartFile multipartFile, String description) throws IOException;

    /**
     * 分页获取文件列表
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDirection 排序方向 (asc/desc)
     * @return 文件的分页结果
     */
    Page<FileDTO> getFilePage(int page, int size, String sortBy, String sortDirection);

    /**
     * 根据ID更新文件信息 (名称和描述)
     * @param id 文件ID
     * @param updateDTO 更新数据
     * @return 更新后的文件DTO
     */
    FileDTO updateFile(Long id, FileUpdateDTO updateDTO);

    /**
     * 根据ID获取文件实体 (用于下载)
     * @param id 文件ID
     * @return 文件实体
     */
    File getFileById(Long id);

    /**
     * 根据ID删除文件及元数据
     * @param id 文件ID
     */
    void deleteFile(Long id);
}