package com.tech.task.dto.response;

public class JwtAuthenticationResponse {

    private String message;
    private String username;
    private String jwt;
    private String refreshToken;

    public JwtAuthenticationResponse() {
    }

    public JwtAuthenticationResponse(String message, String username, String jwt, String refreshToken) {
        this.message = message;
        this.username = username;
        this.jwt = jwt;
        this.refreshToken = refreshToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
