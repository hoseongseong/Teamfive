package com.example.teamfive;

//사용자 계정 정보 모델 클래스

public class UserAccount {
    private String idToken; //Firebase Uid
    private String Email;
    private String Password;

    public UserAccount() {  }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}