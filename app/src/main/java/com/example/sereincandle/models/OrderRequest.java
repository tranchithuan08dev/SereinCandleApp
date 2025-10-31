// File: OrderRequest.java
package com.example.sereincandle.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderRequest {
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("addressLine")
    private String addressLine;
    @SerializedName("city")
    private String city;
    @SerializedName("district")
    private String district;
    @SerializedName("ward")
    private String ward;
    @SerializedName("items")
    private List<OrderItemRequest> items; // Danh sách sản phẩm
    @SerializedName("paymentMethodId")
    private int paymentMethodId;
    @SerializedName("note")
    private String note;
    @SerializedName("voucherCode")
    private String voucherCode;

    public OrderRequest(String fullName, String phone, String addressLine, String city, String district, String ward, List<OrderItemRequest> items) {
        this.fullName = fullName;
        this.phone = phone;
        this.addressLine = addressLine;
        this.city = city;
        this.district = district;
        this.ward = ward;
        this.items = items;
        // Đặt giá trị mặc định theo yêu cầu
        this.paymentMethodId = 1; // Thanh toán khi nhận hàng
        this.note = "Giao hàng giờ hành chính";
        this.voucherCode = null; // Có thể để null nếu không nhập
    }
}