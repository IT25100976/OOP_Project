package com.DailyMart.demo.AdminManagement;

public class AdminCredentials {
    private String username;
    private String password;

    public AdminCredentials() {}

    public AdminCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

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
}
