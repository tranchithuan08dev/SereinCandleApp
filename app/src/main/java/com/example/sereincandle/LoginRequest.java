package com.example.sereincandle;

// Import nếu bạn dùng thư viện Gson/Serializable
// import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Không cần getter/setter nếu chỉ dùng để gửi đi
}