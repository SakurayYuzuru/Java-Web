package com.sakuray.dto.page;

public class PageRequestDTO {
    private int page; // 第几页，从0开始
    private int size; // 每页数量

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
