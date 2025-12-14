package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto;

// backend/src/main/java/com/smartpath/auth/dto/LoginRequest.java

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
