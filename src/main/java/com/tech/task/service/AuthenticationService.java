package com.tech.task.service;

import com.tech.task.dto.request.SignInRequest;
import com.tech.task.dto.request.SignUpRequest;
import com.tech.task.dto.response.JwtAuthenticationResponse;

public interface AuthenticationService {

    public JwtAuthenticationResponse signUp(SignUpRequest request);

    public JwtAuthenticationResponse signIn(SignInRequest request);
}
