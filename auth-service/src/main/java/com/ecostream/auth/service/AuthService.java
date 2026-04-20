package com.ecostream.auth.service;

import com.ecostream.auth.dto.AuthResponse;
import com.ecostream.auth.dto.LoginRequest;
import com.ecostream.auth.dto.RegisterRequest;
import com.ecostream.auth.model.AppUser;
import com.ecostream.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Enregistre un nouvel utilisateur.
     * Le mot de passe est hashé avec BCrypt avant persistance.
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Username already exists: " + request.getUsername());
        }

        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getUsername());

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return buildResponse(token, user);
    }

    /**
     * Authentifie un utilisateur et retourne un token JWT.
     */
    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        log.info("User logged in: {}", user.getUsername());

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return buildResponse(token, user);
    }

    private AuthResponse buildResponse(String token, AppUser user) {
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }
}