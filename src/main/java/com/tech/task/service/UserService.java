package com.tech.task.service;

import com.tech.task.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    public User getByUsername(String username);

    public UserDetailsService userDetailsService();
}
