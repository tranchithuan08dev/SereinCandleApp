package com.example.sereincandle;

import com.google.gson.annotations.SerializedName;

// Đảm bảo tệp này nằm trong gói com.example.sereincandle

public class UserData {

    // Ánh xạ trường "token"
    @SerializedName("token")
    private String token;

    // Ánh xạ trường "email"
    @SerializedName("email")
    private String email;

    // Ánh xạ trường "fullName"
    @SerializedName("fullName")
    private String fullName;

    // Ánh xạ trường "roleName"
    @SerializedName("roleName")
    private String roleName;

    // Getters
    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRoleName() {
        return roleName;
    }
}