package com.example.sereincandle.models;

import com.google.gson.annotations.SerializedName;

public class OrderItemRequest {
    @SerializedName("productId")
    private int productId;

    @SerializedName("quantity")
    private int quantity;

    public OrderItemRequest(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
