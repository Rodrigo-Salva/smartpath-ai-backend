package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
