package com.tech.task.controller;

import com.tech.task.dto.request.SignInRequest;
import com.tech.task.dto.request.SignUpRequest;
import com.tech.task.dto.response.JwtAuthenticationResponse;
import com.tech.task.service.AuthenticationService;
import com.tech.task.service.impl.AuthenticationServiceImpl;
import com.tech.task.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationServiceImpl authenticationService;

    public AuthController(AuthenticationServiceImpl authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody SignUpRequest request) {
        JwtAuthenticationResponse jwtResponse = authenticationService.signUp(request);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody SignInRequest request) {
        JwtAuthenticationResponse jwtResponse = authenticationService.signIn(request);
        return ResponseEntity.ok(jwtResponse);
    }
}
