// File: CartItem.java (Dùng cho danh sách item trong giỏ hàng)
package com.example.sereincandle.models;

import com.google.gson.annotations.SerializedName;

public class CartItem {
    @SerializedName("productId")
    private int productId;
    @SerializedName("productName")
    private String productName;
    @SerializedName("productSku")
    private String productSku;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("priceAtAdd")
    private double priceAtAdd;
    @SerializedName("imageUrl")
    private String imageUrl;

    // Cần các Getter để hiển thị dữ liệu
    public int getProductId() {
        return productId;
    }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPriceAtAdd() { return priceAtAdd; }
    public String getImageUrl() { return imageUrl; }
    // ... (Thêm các Getter khác nếu cần)
}