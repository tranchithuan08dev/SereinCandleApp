// File: CartItemRequest.java
package com.example.sereincandle.models;

import com.google.gson.annotations.SerializedName;

public class CartItemRequest {
    @SerializedName("productId")
    private int productId;

    @SerializedName("quantity")
    private int quantity; // Mặc định là 1

    public CartItemRequest(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Không cần getter/setter nếu không sử dụng sau khi tạo request
}