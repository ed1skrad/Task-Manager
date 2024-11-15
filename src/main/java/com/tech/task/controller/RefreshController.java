package com.tech.task.controller;

import com.tech.task.dto.request.RefreshTokenRequest;
import com.tech.task.dto.response.JwtAuthenticationResponse;
import com.tech.task.service.impl.RefreshTokenServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refresh")
public class RefreshController {

    private final RefreshTokenServiceImpl refreshTokenService;

    public RefreshController(RefreshTokenServiceImpl refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        JwtAuthenticationResponse jwtResponse = refreshTokenService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(jwtResponse);
    }
}
