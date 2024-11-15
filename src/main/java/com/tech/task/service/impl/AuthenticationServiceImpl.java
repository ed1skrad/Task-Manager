package com.tech.task.service.impl;

import com.tech.task.dto.request.SignInRequest;
import com.tech.task.dto.request.SignUpRequest;
import com.tech.task.dto.response.JwtAuthenticationResponse;
import com.tech.task.exception.EmailInUseException;
import com.tech.task.exception.RoleNotFoundException;
import com.tech.task.exception.UsernameTakenException;
import com.tech.task.model.User;
import com.tech.task.model.role.Role;
import com.tech.task.model.role.RoleEnum;
import com.tech.task.repository.RoleRepository;
import com.tech.task.repository.UserRepository;
import com.tech.task.service.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserServiceImpl userService;
    private final JwtServiceImpl jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenServiceImpl refreshTokenService;

    public AuthenticationServiceImpl(UserServiceImpl userService, JwtServiceImpl jwtService,
                                     PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                                     UserRepository userRepository, RoleRepository roleRepository,
                                     RefreshTokenServiceImpl refreshTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameTakenException("Error: username is taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailInUseException("Error: email already in use!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role roleInactive = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException("Error. Role inactive not found."));
        List<Role> roles = new ArrayList<>();
        roles.add(roleInactive);
        user.setRoles(roles);

        userRepository.save(user);

        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.generateRefreshToken(user);
        return new JwtAuthenticationResponse("Success!", request.getUsername(), jwt, refreshToken);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getEmail());

        refreshTokenService.deleteRefreshToken((User) user);
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.generateRefreshToken(user);
        return new JwtAuthenticationResponse("Success", request.getEmail(), jwt, refreshToken);
    }
}
