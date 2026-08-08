package com.example.springbootdemo.dto;

import com.example.springbootdemo.entity.User;

import java.util.List;

public class UserPageResult {

    private List<User> records;
    private long total;
    private int page;
    private int pageSize;
    private long totalPages;

    public UserPageResult(
            List<User> records,
            long total,
            int page,
            int pageSize,
            long totalPages
    ) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
    }

    public List<User> getRecords() {
        return records;
    }

    public long getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalPages() {
        return totalPages;
    }
}