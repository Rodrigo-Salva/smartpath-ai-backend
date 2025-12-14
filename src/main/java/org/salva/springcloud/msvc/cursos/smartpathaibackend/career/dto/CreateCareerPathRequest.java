package org.salva.springcloud.msvc.cursos.smartpathaibackend.career.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCareerPathRequest {

    @NotBlank(message = "El título es obligatorio")
    private String title;

    private String description;

    @NotBlank(message = "El rol objetivo es obligatorio")
    private String targetRole;

    @NotBlank(message = "El nivel de dificultad es obligatorio")
    private String difficultyLevel; // BEGINNER, INTERMEDIATE, ADVANCED

    @NotNull(message = "La duración estimada es obligatoria")
    private Integer estimatedDurationWeeks;

    private List<String> requiredSkills;
    private List<String> prerequisites;
}
