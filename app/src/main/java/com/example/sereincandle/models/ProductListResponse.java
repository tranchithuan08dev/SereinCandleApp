package com.example.sereincandle.models;

// File: ProductListResponse.java

import java.util.List;

public class ProductListResponse {
    private List<Product> data;
    private int totalCount;
    private int pageNumber;
    private int pageSize;

    public List<Product> getData() { return data; }
    public int getTotalCount() { return totalCount; }
    public int getPageNumber() { return pageNumber; }
    public int getPageSize() { return pageSize; }
}