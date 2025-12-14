package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto;

// backend/src/main/java/com/smartpath/auth/dto/RegisterRequest.java

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String fullName;

    @NotBlank(message = "El nivel de experiencia es obligatorio")
    private String experienceLevel; // STUDENT, JUNIOR, MID, CAREER_CHANGE
}

