package com.home.preorder.service.specification;

import lombok.Data;

@Data
public class TaskSpecParams {
    private int pageIndex = 1;
    private Integer pageSize = 10;
    private String search;
    private String sort = "createdAtDesc";
    private String userId;
    private String status;
    private String categoryName;
    private final int MaxPageSize = 20;

    public void setPageSize(int pageSize) {
        this.pageSize = Math.min(Math.max(pageSize, 1), MaxPageSize);
    }

    public void setSearch(String search) {
        this.search = search != null ? search.toLowerCase() : null;
    }
}
