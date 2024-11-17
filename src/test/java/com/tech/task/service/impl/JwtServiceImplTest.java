package com.tech.task.service.impl;

import com.tech.task.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    private User user;
    private Key key;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        String jwtSigningKey = "53A73E5F1C4E0A2D3B5F2D784E6A1B423D6F247D1F6E5C3A596D635A7532785553A73E5F1C4E0A2D3B5F2D784E6A1B423D6F247D1F6E5C3A596D635A75327855";
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", jwtSigningKey);

        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    void testExtractUserName() {
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String extractedUsername = jwtService.extractUserName(token);
        assertEquals(user.getUsername(), extractedUsername);
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(user);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(user.getUsername(), claims.getSubject());
        assertEquals(user.getId().intValue(), claims.get("id", Integer.class)); // Явно указываем тип Integer
        assertEquals(user.getEmail(), claims.get("email"));
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void testIsTokenValid() {
        String token = jwtService.generateToken(user);

        boolean isValid = jwtService.isTokenValid(token, user);
        assertTrue(isValid);
    }
}
