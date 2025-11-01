package com.example.sereincandle.models;

/**
 * Request để cập nhật trạng thái đơn hàng
 * Mapping với Backend: PUT /api/Order/{orderId}/status
 */
public class OrderStatusRequest {
    private int statusId;  // OrderStatus.StatusId (required)
    // Values: 1: Pending, 2: Preparing, 3: Shipped, 4: Completed, 5: Cancelled

    public OrderStatusRequest(int statusId) {
        this.statusId = statusId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}
