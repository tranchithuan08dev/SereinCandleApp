package com.example.sereincandle.models;

import com.example.sereincandle.ProductAttribute;
import com.example.sereincandle.ProductImage;

import java.util.List;

public class Product {
    private int productId;
    private String name;
    private String sku;
    private String description;
    private String ingredients;
    private String burnTime;
    private int price;
    private boolean isActive;
    private String categoryName;
    private List<ProductAttribute> attributes;
    private List<ProductImage> images;

    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public String getDescription() { return description; }
    public String getIngredients() { return ingredients; }
    public String getBurnTime() { return burnTime; }
    public int getPrice() { return price; }
    public boolean isActive() { return isActive; }
    public String getCategoryName() { return categoryName; }
    public List<ProductAttribute> getAttributes() { return attributes; }
    public List<ProductImage> getImages() { return images; }

    // Phương thức tiện ích để lấy URL ảnh chính
    public String getPrimaryImageUrl() {
        if (images != null) {
            for (ProductImage image : images) {
                if (image.isPrimary()) {
                    return image.getImageUrl();
                }
            }
        }
        // Trả về ảnh đầu tiên nếu không có ảnh nào là primary
        if (images != null && !images.isEmpty()) {
            return images.get(0).getImageUrl();
        }
        return null; // Không có ảnh
    }
}