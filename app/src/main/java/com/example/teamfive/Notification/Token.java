package com.example.teamfive.Notification;

public class Token {
    private String Token;
    private String UID;

    public Token() {
    }

    public Token(String token) {
        this.Token = token;
    }

    public Token(String token, String UID) {
        Token = token;
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        this.Token = token;
    }
}
