package com.sakuray.dto.file;

import com.sakuray.dto.page.PageRequestDTO; // 导入基础DTO

/**
 * 文件分页查询 DTO (File Page Request DTO)
 * 继承 PageRequestDTO，复用 page 和 size 字段，并添加文件特有的排序字段。
 */
public class FilePageDTO extends PageRequestDTO { // <-- 继承基础DTO

    private String sortBy = "uploadTime"; // 默认按上传时间排序

    private String sortDirection = "desc"; // 默认降序

    // --- Getters and Setters (page 和 size 已继承) ---

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}