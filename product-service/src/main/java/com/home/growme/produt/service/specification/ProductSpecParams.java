package com.home.growme.produt.service.specification;

import lombok.Data;

@Data
public class ProductSpecParams {

    private Integer pageSize;
    private String search;
    private String sort;
    private String owner;
    private String categoryId;
    private String title;
    private final int MaxPageSize=20;
    private int pageIndex=1;

    public void setPageSize(int pageSize) {
        if(pageSize>MaxPageSize){
            this.pageSize=MaxPageSize;
        }else if(pageSize<1){
            this.pageSize=1;
        }else {
            this.pageSize=pageSize;
        }
    }

    public void setSearch(String search) {
        this.search = search.toLowerCase();
    }
}
