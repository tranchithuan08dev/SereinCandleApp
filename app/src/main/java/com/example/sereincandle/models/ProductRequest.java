package com.example.sereincandle.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model để tạo/cập nhật sản phẩm
 * Mapping với Backend: InsertProductDto
 * Backend sử dụng PascalCase (Name, Description) nên cần @SerializedName
 */
public class ProductRequest {
    @SerializedName("Name")
    private String name;              // required
    
    @SerializedName("SKU")
    private String sku;               // optional
    
    @SerializedName("ShortDescription")
    private String shortDescription;  // optional
    
    @SerializedName("Description")
    private String description;       // required
    
    @SerializedName("Ingredients")
    private String ingredients;       // optional
    
    @SerializedName("BurnTime")
    private String burnTime;          // optional
    
    @SerializedName("Price")
    private double price;             // required (decimal)
    
    @SerializedName("CategoryId")
    private int categoryId;           // required
    
    @SerializedName("Attributes")
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
