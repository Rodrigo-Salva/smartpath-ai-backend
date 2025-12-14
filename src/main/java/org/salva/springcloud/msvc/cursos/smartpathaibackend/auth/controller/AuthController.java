package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.controller;

// backend/src/main/java/com/smartpath/auth/controller/AuthController.java

import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto.AuthResponse;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto.LoginRequest;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto.RegisterRequest;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.service.AuthService;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro y login de usuarios")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario",
            description = "Crea una cuenta nueva con email y contraseña")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);

        ApiResponse response = ApiResponse.success(
                "Usuario registrado exitosamente. ¡Bienvenido a SmartPath AI!",
                authResponse
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Autentica al usuario y retorna un JWT token")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        ApiResponse response = ApiResponse.success(
                "Inicio de sesión exitoso. ¡Bienvenido de vuelta!",
                authResponse
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check",
            description = "Verifica que el servicio de autenticación esté funcionando")
    public ResponseEntity<ApiResponse> health() {
        ApiResponse response = ApiResponse.success(
                "Servicio de autenticación funcionando correctamente",
                null
        );
        return ResponseEntity.ok(response);
    }
}

