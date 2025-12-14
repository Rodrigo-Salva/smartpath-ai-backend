package org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.dto;

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
public class CreateResourceRequest {

    @NotBlank(message = "El título es obligatorio")
    private String title;

    private String description;

    @NotBlank(message = "El tipo de recurso es obligatorio")
    private String resourceType; // COURSE, BOOK, VIDEO, ARTICLE, TUTORIAL, PROJECT

    private String provider;

    @NotBlank(message = "La URL es obligatoria")
    private String url;

    private String difficultyLevel;

    private Integer estimatedHours;

    @NotNull(message = "Indicar si es gratis es obligatorio")
    private Boolean isFree;

    private String price;
    private String rating;
    private List<String> tags;
}