package com.tech.task.controller;

import com.tech.task.dto.request.RefreshTokenRequest;
import com.tech.task.dto.response.JwtAuthenticationResponse;
import com.tech.task.service.impl.RefreshTokenServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refresh")
@Tag(name = "Refresh Token Management", description = "Endpoints for managing refresh tokens")
public class RefreshController {

    private final RefreshTokenServiceImpl refreshTokenService;

    public RefreshController(RefreshTokenServiceImpl refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Operation(summary = "Refresh JWT token", description = "Refresh the JWT token using a refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/token")
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        JwtAuthenticationResponse jwtResponse = refreshTokenService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(jwtResponse);
    }
}
