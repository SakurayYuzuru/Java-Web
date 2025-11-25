package com.sakuray.controller;

import com.sakuray.dto.file.FileDTO;
import com.sakuray.dto.file.FilePageDTO;
import com.sakuray.dto.file.FileUpdateDTO;
import com.sakuray.entity.File;
import com.sakuray.service.FileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path; // 修复: 导入 java.nio.file.Path
import java.nio.file.Paths; // 修复: 导入 java.nio.file.Paths

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * 文件管理 REST Controller
 * 提供文件上传、分页查询、信息更新和下载接口。
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * [上传] 文件上传接口
     * POST /api/files/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<FileDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "上传文件不能为空");
        }
        try {
            FileDTO fileDTO = fileService.uploadFile(file, description);
            return ResponseEntity.ok(fileDTO);
        } catch (IOException e) {
            throw new ResponseStatusException(BAD_REQUEST, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * [查询] 分页查询文件列表
     * GET /api/files?page=0&size=10&sortBy=uploadTime
     */
    @GetMapping
    public ResponseEntity<Page<FileDTO>> getFiles(@Valid FilePageDTO pageDTO) {
        Page<FileDTO> page = fileService.getFilePage(
                pageDTO.getPage(),
                pageDTO.getSize(),
                pageDTO.getSortBy(),
                pageDTO.getSortDirection()
        );
        return ResponseEntity.ok(page);
    }

    /**
     * [编辑] 更新文件元数据 (名称和描述)
     * PUT /api/files/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<FileDTO> updateFile(
            @PathVariable Long id,
            @Valid @RequestBody FileUpdateDTO updateDTO) {
        FileDTO updatedFile = fileService.updateFile(id, updateDTO);
        return ResponseEntity.ok(updatedFile);
    }

    /**
     * [下载] 下载文件
     * GET /api/files/download/{id}
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        File file = fileService.getFileById(id);
        Path path = Paths.get(file.getPath());
        Resource resource;

        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(NOT_FOUND, "文件路径无效: " + file.getPath());
        }

        if (resource.exists() || resource.isReadable()) {
            // 使用 URI 编码文件名，确保中文文件名在响应头中正确显示
            String encodedFilename = java.net.URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .body(resource);
        } else {
            throw new ResponseStatusException(NOT_FOUND, "文件未找到或无法读取: " + file.getName());
        }
    }

    /**
     * [删除] 删除文件及元数据
     * DELETE /api/files/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}