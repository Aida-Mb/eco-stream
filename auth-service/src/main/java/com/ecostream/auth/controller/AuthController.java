package com.ecostream.auth.controller;

import com.ecostream.auth.dto.AuthResponse;
import com.ecostream.auth.dto.LoginRequest;
import com.ecostream.auth.dto.RegisterRequest;
import com.ecostream.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints REST de l'Auth Service.
 *
 *  POST /auth/register  — crée un compte, retourne un JWT
 *  POST /auth/login     — authentifie, retourne un JWT
 *  GET  /auth/health    — health check
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Crée un nouvel utilisateur.
     * Body : { "username": "alice", "password": "secret123" }
     * Réponse 201 : { "token": "eyJ...", "username": "alice", "role": "ROLE_USER", "expiresIn": 86400000 }
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * Authentifie un utilisateur existant.
     * Body : { "username": "alice", "password": "secret123" }
     * Réponse 200 : { "token": "eyJ...", "username": "alice", "role": "ROLE_USER", "expiresIn": 86400000 }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Health check simple.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\":\"UP\"}");
    }
}