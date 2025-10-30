package com.example.sereincandle;

import com.google.gson.annotations.SerializedName;

// Đảm bảo tệp này nằm trong gói com.example.sereincandle

public class LoginResponse {

    // Ánh xạ trường "success"
    @SerializedName("success")
    private boolean success;

    // Ánh xạ trường "message" (Thông báo chung)
    @SerializedName("message")
    private String message;

    // Ánh xạ trường "data" thành một đối tượng Dữ liệu (User Data)
    @SerializedName("data")
    private UserData data;

    // Trường lỗi (Giả định server có thể trả về lỗi thay vì data)
    private String error;

    // Constructor, Getters, Setters (Nếu cần)

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public UserData getData() {
        return data;
    }

    // Hàm tiện ích để truy cập token dễ hơn trong MainActivity
    public String getToken() {
        return (data != null) ? data.getToken() : null;
    }

    public String getError() {
        return error; // Giả định trường lỗi có thể được điền khi success=false
    }
}