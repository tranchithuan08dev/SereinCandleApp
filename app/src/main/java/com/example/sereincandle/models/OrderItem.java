package com.example.sereincandle.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Model cho item trong đơn hàng
 * Mapping với SQL: OrderItem + Product (JOIN)
 */
public class OrderItem implements Serializable {
    @SerializedName("orderItemId")
    private int orderItemId;  // OrderItem.OrderItemId

    @SerializedName("productId")
    private int productId;  // OrderItem.ProductId

    @SerializedName("productName")
    private String productName;  // Product.Name (JOIN)

    @SerializedName("productSku")
    private String productSku;  // Product.SKU (JOIN)

    @SerializedName("quantity")
    private int quantity;  // OrderItem.Quantity

    @SerializedName("unitPrice")
    private double unitPrice;  // OrderItem.UnitPrice

    @SerializedName("totalPrice")
    private double totalPrice;  // OrderItem.TotalPrice (calculated: Quantity * UnitPrice)

    @SerializedName("productImageUrl")
    private String productImageUrl;  // ProductImage.ImageUrl (JOIN, primary image)

    // Getters and Setters
    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }
}
