package com.tech.task.service;

import com.tech.task.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface RefreshTokenService {

    public String generateRefreshToken(UserDetails userDetails);

    public boolean isRefreshTokenValid(String token);

    public UserDetails getUserDetailsFromRefreshToken(String refreshToken);

    public String generateTokenFromRefreshToken(String refreshToken);

    public void deleteRefreshToken(User user);

    public void deleteRefreshTokenById(Long id);
}
