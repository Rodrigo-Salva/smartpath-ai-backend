package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.service;

// backend/src/main/java/com/smartpath/auth/service/AuthService.java
import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto.AuthResponse;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto.LoginRequest;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto.RegisterRequest;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.exception.BadRequestException;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.exception.UnauthorizedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validar que el email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "El email ya está registrado. Intenta con otro email o inicia sesión."
            );
        }

        // Validar nivel de experiencia
        validateExperienceLevel(request.getExperienceLevel());

        // Crear usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setExperienceLevel(request.getExperienceLevel().toUpperCase());
        user.setIsActive(true);
        user.setEmailVerified(false);

        // Guardar en BD
        User savedUser = userRepository.save(user);

        // Generar JWT
        String token = jwtService.generateToken(savedUser.getEmail());

        // Retornar respuesta
        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Buscar usuario por email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException(
                        "Email o contraseña incorrectos. Verifica tus credenciales."
                ));

        // Verificar que el usuario esté activo
        if (!user.getIsActive()) {
            throw new UnauthorizedException(
                    "Tu cuenta está desactivada. Contacta a soporte."
            );
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException(
                    "Email o contraseña incorrectos. Verifica tus credenciales."
            );
        }

        // Generar JWT
        String token = jwtService.generateToken(user.getEmail());

        // Retornar respuesta
        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName()
        );
    }

    private void validateExperienceLevel(String level) {
        String upperLevel = level.toUpperCase();
        if (!upperLevel.equals("STUDENT") &&
                !upperLevel.equals("JUNIOR") &&
                !upperLevel.equals("MID") &&
                !upperLevel.equals("CAREER_CHANGE")) {
            throw new BadRequestException(
                    "Nivel de experiencia inválido. Opciones: STUDENT, JUNIOR, MID, CAREER_CHANGE"
            );
        }
    }
}

