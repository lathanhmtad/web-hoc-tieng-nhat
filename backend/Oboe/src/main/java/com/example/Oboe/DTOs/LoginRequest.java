package com.example.Oboe.DTOs;




//@Data
public class LoginRequest {
    private String userName;
    private String passWord;

    public String getEmail() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
