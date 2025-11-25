package com.sakuray.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.sakuray.dto.file.FileDTO;
import com.sakuray.dto.file.FileUpdateDTO;
import com.sakuray.entity.File;
import com.sakuray.repo.FileRepo;
import com.sakuray.service.FileService;

/**
 * 文件业务服务实现类 (File Business Service Implementation)
 */
@Service
public class FileServiceImpl implements FileService {

    // 假设文件存储在系统临时目录下的 /uploads 文件夹
    private final String UPLOAD_DIR = System.getProperty("java.io.tmpdir") + "/uploads/";

    @Autowired
    private FileRepo fileRepo;

    public FileServiceImpl() {
        // 确保上传目录存在
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录: " + UPLOAD_DIR, e);
        }
    }

    @Override
    @Transactional
    public FileDTO uploadFile(MultipartFile multipartFile, String description) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        // 使用 UUID 确保文件名唯一，防止覆盖
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = Paths.get(UPLOAD_DIR, uniqueFilename);

        // 1. 实际文件存储到文件系统
        Files.copy(multipartFile.getInputStream(), filePath);

        // 2. 数据库元数据保存
        File file = new File(
                originalFilename,
                filePath.toString(), // 存储文件的绝对路径
                description,
                multipartFile.getSize(),
                LocalDateTime.now()
        );

        File savedFile = fileRepo.save(file);
        return FileDTO.fromEntity(savedFile);
    }

    @Override
    public Page<FileDTO> getFilePage(int page, int size, String sortBy, String sortDirection) {
        // 构造排序对象
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        // 构造分页请求对象
        PageRequest pageable = PageRequest.of(page, size, sort);

        // 从数据库获取分页数据，并映射到 DTO
        return fileRepo.findAll(pageable).map(FileDTO::fromEntity);
    }

    @Override
    @Transactional
    public FileDTO updateFile(Long id, FileUpdateDTO updateDTO) {
        File file = fileRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "未找到ID为 " + id + " 的文件元数据"));

        // 更新可编辑的字段
        if (updateDTO.getName() != null) {
            file.setName(updateDTO.getName());
        }
        if (updateDTO.getDescription() != null) {
            file.setDescription(updateDTO.getDescription());
        }

        File updatedFile = fileRepo.save(file);
        return FileDTO.fromEntity(updatedFile);
    }

    @Override
    public File getFileById(Long id) {
        return fileRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "未找到ID为 " + id + " 的文件元数据"));
    }

    @Override
    @Transactional
    public void deleteFile(Long id) {
        File file = fileRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "未找到ID为 " + id + " 的文件元数据"));

        try {
            // 1. 从文件系统删除实际文件
            Files.deleteIfExists(Paths.get(file.getPath()));
        } catch (IOException e) {
            // 记录日志，但不阻止删除数据库记录，除非文件删除是硬性要求
            System.err.println("删除文件失败: " + file.getPath() + ", 错误信息: " + e.getMessage());
        }

        // 2. 删除数据库记录
        fileRepo.delete(file);
    }
}