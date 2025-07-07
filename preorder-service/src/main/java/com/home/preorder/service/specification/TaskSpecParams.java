package com.home.preorder.service.specification;

import lombok.Data;

@Data
public class TaskSpecParams {
    private Integer pageSize;
    private String search;
    private String sort;
    private String userId;
    private String status;
    private final int MaxPageSize = 20;
    private int pageIndex = 1;

    public void setPageSize(int pageSize) {
        this.pageSize = Math.min(Math.max(pageSize, 1), MaxPageSize);
    }

    public void setSearch(String search) {
        this.search = search != null ? search.toLowerCase() : null;
    }
}
