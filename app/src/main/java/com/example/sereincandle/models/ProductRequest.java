package com.example.sereincandle.models;

import java.util.List;

/**
 * Model để tạo/cập nhật sản phẩm
 * Mapping với Backend: InsertProductDto
 */
public class ProductRequest {
    private String name;              // required
    private String sku;               // optional
    private String shortDescription;  // optional
    private String description;       // required
    private String ingredients;       // optional
    private String burnTime;          // optional
    private double price;             // required (decimal)
    private int categoryId;           // required
    private List<ProductAttributeValue> attributes;  // optional

    // Constructors
    public ProductRequest() {
    }

    public ProductRequest(String name, String description, double price, int categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(String burnTime) {
        this.burnTime = burnTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public List<ProductAttributeValue> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ProductAttributeValue> attributes) {
        this.attributes = attributes;
    }
}
