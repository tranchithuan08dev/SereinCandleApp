package com.example.sereincandle.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Model đơn hàng cho Admin
 * Mapping với Backend response: GET /api/Order/admin
 */
public class Order implements Serializable {
    @SerializedName("orderId")
    private int orderId;

    @SerializedName("orderCode")
    private String orderCode;

    @SerializedName("statusName")
    private String statusName;  // 'Pending', 'Preparing', 'Shipped', 'Completed', 'Cancelled'

    @SerializedName("paymentMethodName")
    private String paymentMethodName;  // 'COD', 'VNPay', 'MoMo', 'ZaloPay', 'BankTransfer'

    @SerializedName("userId")
    private Integer userId;  // nullable

    @SerializedName("customerFullName")
    private String customerFullName;

    @SerializedName("customerEmail")
    private String customerEmail;

    @SerializedName("recipientAddress")
    private String recipientAddress;  // Địa chỉ nhận hàng (full address string)

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("discountAmount")
    private double discountAmount;

    @SerializedName("shippingFee")
    private double shippingFee;

    @SerializedName("createdAt")
    private Date createdAt;  // DateTime

    @SerializedName("items")
    private List<OrderItem> items;  // OrderItemDetailDto (có đầy đủ thông tin)

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    // Helper method để lấy statusId từ statusName (cho UI)
    public int getStatusId() {
        if (statusName == null) return 1;
        switch (statusName.toLowerCase()) {
            case "pending": return 1;
            case "preparing": return 2;
            case "shipped": return 3;
            case "completed": return 4;
            case "cancelled": return 5;
            default: return 1;
        }
    }

    public boolean isCancelled() {
        return "Cancelled".equalsIgnoreCase(statusName);
    }
}
