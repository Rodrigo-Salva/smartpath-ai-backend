package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.service.UserProfileService;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Gestión de perfil de usuario")
public class UserProfileController {

    private final UserProfileService profileService;
    private final UserRepository userRepository;

    @GetMapping("/profile")
    @Operation(summary = "Obtener mi perfil completo")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getMyProfile(
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        UserProfileDTO profile = profileService.getUserProfile(userId);

        return ResponseEntity.ok(ApiResponse.success("Perfil obtenido", profile));
    }

    @PutMapping("/profile")
    @Operation(summary = "Actualizar mi perfil")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        UserProfileDTO updated = profileService.updateProfile(userId, request);

        return ResponseEntity.ok(ApiResponse.success("Perfil actualizado exitosamente", updated));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Cambiar mi contraseña")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        profileService.changePassword(userId, request);

        return ResponseEntity.ok(ApiResponse.success("Contraseña cambiada exitosamente", null));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Obtener dashboard personalizado con estadísticas")
    public ResponseEntity<ApiResponse<UserDashboardDTO>> getDashboard(
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);
        UserDashboardDTO dashboard = profileService.getDashboard(userId);

        return ResponseEntity.ok(ApiResponse.success("Dashboard obtenido", dashboard));
    }

    @DeleteMapping("/account")
    @Operation(summary = "Eliminar mi cuenta permanentemente")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @RequestParam String confirmEmail,
            Authentication authentication) {

        String email = authentication.getName();

        if (!email.equals(confirmEmail)) {
            throw new RuntimeException("El email de confirmación no coincide");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Eliminar usuario (soft delete o hard delete según tu necesidad)
        userRepository.delete(user);

        return ResponseEntity.ok(ApiResponse.success("Cuenta eliminada exitosamente", null));
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }
}

