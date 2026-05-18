package com.bnpp.assessment.dto;

public class RegisterUserDto {

    private String username;
    private String password;
    private String email;
    private String pan;
    private String aadhar;
    private String phone;

    // --- Constructors ---
    public RegisterUserDto() {
    }

    public RegisterUserDto(String username, String password, String email, String pan, String aadhar, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.pan = pan;
        this.aadhar = aadhar;
        this.phone = phone;
    }

    // --- Getters and Setters ---
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}