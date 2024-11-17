package com.tech.task.repository;

import com.tech.task.model.User;
import com.tech.task.model.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteById(Long id);

    boolean existsByUser(User user);
}

