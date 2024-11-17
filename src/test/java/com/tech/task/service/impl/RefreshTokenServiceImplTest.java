package com.tech.task.service.impl;

import com.tech.task.dto.response.JwtAuthenticationResponse;
import com.tech.task.exception.InvalidRefreshTokenException;
import com.tech.task.model.User;
import com.tech.task.model.token.RefreshToken;
import com.tech.task.repository.RefreshTokenRepository;
import com.tech.task.utils.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private JwtServiceImpl jwtService;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken("refreshtoken");
        refreshToken.setExpiryDate(Instant.now().plusMillis(60 * 60 * 24 * 7 * 1000));
    }

    @Test
    void testGenerateRefreshToken() {
        when(refreshTokenRepository.existsByUser(user)).thenReturn(false);
        when(tokenGenerator.generateToken()).thenReturn("newrefreshtoken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        String token = refreshTokenService.generateRefreshToken(user);

        assertEquals("newrefreshtoken", token);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void testIsRefreshTokenValid_ValidToken() {
        when(refreshTokenRepository.findByToken("refreshtoken")).thenReturn(Optional.of(refreshToken));

        boolean isValid = refreshTokenService.isRefreshTokenValid("refreshtoken");
        assertTrue(isValid);
    }

    @Test
    void testIsRefreshTokenValid_InvalidToken() {
        when(refreshTokenRepository.findByToken("invalidtoken")).thenReturn(Optional.empty());

        boolean isValid = refreshTokenService.isRefreshTokenValid("invalidtoken");
        assertFalse(isValid);
    }

    @Test
    void testGetUserDetailsFromRefreshToken() {
        when(refreshTokenRepository.findByToken("refreshtoken")).thenReturn(Optional.of(refreshToken));

        UserDetails userDetails = refreshTokenService.getUserDetailsFromRefreshToken("refreshtoken");
        assertEquals(user, userDetails);
    }

    @Test
    void testGenerateTokenFromRefreshToken() {
        when(refreshTokenRepository.findByToken("refreshtoken")).thenReturn(Optional.of(refreshToken));
        when(jwtService.generateToken(user)).thenReturn("jwttoken");

        String token = refreshTokenService.generateTokenFromRefreshToken("refreshtoken");
        assertEquals("jwttoken", token);
    }

    @Test
    void testRefreshToken_ValidToken() {
        when(refreshTokenRepository.findByToken("refreshtoken")).thenReturn(Optional.of(refreshToken));
        when(jwtService.generateToken(user)).thenReturn("jwttoken");
        when(tokenGenerator.generateToken()).thenReturn("newrefreshtoken");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        JwtAuthenticationResponse response = refreshTokenService.refreshToken("refreshtoken");

        assertEquals("Success", response.getMessage());
        assertEquals("testuser", response.getUsername());
        assertEquals("jwttoken", response.getJwt());
        assertEquals("newrefreshtoken", response.getRefreshToken());

        verify(refreshTokenRepository, times(1)).deleteByUser(user);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void testRefreshToken_InvalidToken() {
        when(refreshTokenRepository.findByToken("invalidtoken")).thenReturn(Optional.empty());

        assertThrows(InvalidRefreshTokenException.class, () -> refreshTokenService.refreshToken("invalidtoken"));
    }

    @Test
    void testDeleteRefreshToken() {
        refreshTokenService.deleteRefreshToken(user);

        verify(refreshTokenRepository, times(1)).deleteByUser(user);
    }
}
