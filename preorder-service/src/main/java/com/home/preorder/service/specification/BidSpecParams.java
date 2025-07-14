package com.home.preorder.service.specification;

import lombok.Data;

@Data
public class BidSpecParams {
    private int pageIndex = 1;
    private Integer pageSize = 10;
    private String sort = "createdAtDesc";
    private String userId;
    private String taskId;
    private String status;
    private final int maxPageSize = 50;

    public void setPageSize(int pageSize) {
        this.pageSize = Math.min(Math.max(pageSize, 1), maxPageSize);
    }
}
