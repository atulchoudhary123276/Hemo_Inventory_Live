package com.example.dto;

import javax.validation.constraints.NotNull;

public class UserLoginDto {
    @NotNull
    private String userName;
    @NotNull
    private String password;
    private String confirmPassword;
    private String dob;

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "UserLoginDto{" +
                "userName='" + userName + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }
}
