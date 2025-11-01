package com.example.sereincandle;

/**
 * Model cho request đăng ký Customer
 */
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String dateOfBirth;  // Format: yyyy-MM-dd

    public RegisterRequest() {
    }

    public RegisterRequest(String email, String password, String fullName, String phone, String dateOfBirth) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
