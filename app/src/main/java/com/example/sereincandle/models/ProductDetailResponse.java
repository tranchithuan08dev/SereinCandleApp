package com.example.sereincandle.models;

public class ProductDetailResponse {
    private boolean success;
    private String message;
    private Product data; // Sử dụng lại lớp Product đã tạo trước đó

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Product getData() { return data; }
}