// File: CartResponse.java
package com.example.sereincandle.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CartResponse {
    @SerializedName("cartId")
    private int cartId;
    @SerializedName("userId")
    private int userId;
    @SerializedName("totalAmount")
    private double totalAmount;
    @SerializedName("items")
    private List<CartItem> items; // Sử dụng lớp CartItem vừa tạo

    // Cần các Getter để hiển thị dữ thị
    public double getTotalAmount() { return totalAmount; }
    public List<CartItem> getItems() { return items; }
    // ... (Thêm các Getter khác nếu cần)
}