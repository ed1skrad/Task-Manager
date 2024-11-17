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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private JwtServiceImpl jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenServiceImpl refreshTokenService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User user;
    private Role role;
    private SignUpRequest signUpRequest;
    private SignInRequest signInRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("encodedpassword");

        role = new Role();
        role.setId(1);
        role.setName(RoleEnum.ROLE_USER);

        signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testuser");
        signUpRequest.setEmail("testuser@example.com");
        signUpRequest.setPassword("password");

        signInRequest = new SignInRequest();
        signInRequest.setEmail("testuser@example.com");
        signInRequest.setPassword("password");
    }

    @Test
    void testSignUp_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwttoken");
        when(refreshTokenService.generateRefreshToken(any(User.class))).thenReturn("refreshtoken");

        JwtAuthenticationResponse response = authenticationService.signUp(signUpRequest);

        assertNotNull(response);
        assertEquals("Success!", response.getMessage());
        assertEquals("testuser", response.getUsername());
        assertEquals("jwttoken", response.getJwt());
        assertEquals("refreshtoken", response.getRefreshToken());

        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
        verify(refreshTokenService, times(1)).generateRefreshToken(any(User.class));
    }

    @Test
    void testSignUp_UsernameTaken() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(UsernameTakenException.class, () -> authenticationService.signUp(signUpRequest));

        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));
        verify(refreshTokenService, never()).generateRefreshToken(any(User.class));
    }

    @Test
    void testSignUp_EmailInUse() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailInUseException.class, () -> authenticationService.signUp(signUpRequest));

        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));
        verify(refreshTokenService, never()).generateRefreshToken(any(User.class));
    }

    @Test
    void testSignUp_RoleNotFound() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(any())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> authenticationService.signUp(signUpRequest));

        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));
        verify(refreshTokenService, never()).generateRefreshToken(any(User.class));
    }

    @Test
    void testSignIn_Success() {
        UserDetailsService userDetailsServiceMock = mock(UserDetailsService.class);
        when(userService.userDetailsService()).thenReturn(userDetailsServiceMock);
        when(userDetailsServiceMock.loadUserByUsername(anyString())).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        doNothing().when(refreshTokenService).deleteRefreshToken(any(User.class));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwttoken");
        when(refreshTokenService.generateRefreshToken(any(User.class))).thenReturn("refreshtoken");

        JwtAuthenticationResponse response = authenticationService.signIn(signInRequest);

        assertNotNull(response);
        assertEquals("Success", response.getMessage());
        assertEquals("testuser@example.com", response.getUsername());
        assertEquals("jwttoken", response.getJwt());
        assertEquals("refreshtoken", response.getRefreshToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsServiceMock, times(1)).loadUserByUsername(anyString());
        verify(refreshTokenService, times(1)).deleteRefreshToken(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
        verify(refreshTokenService, times(1)).generateRefreshToken(any(User.class));
    }
}
