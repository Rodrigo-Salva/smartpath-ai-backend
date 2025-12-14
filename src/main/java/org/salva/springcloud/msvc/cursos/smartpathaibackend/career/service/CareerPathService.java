package org.salva.springcloud.msvc.cursos.smartpathaibackend.career.service;

import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.career.dto.CareerPathDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.career.dto.CreateCareerPathRequest;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.career.model.CareerPath;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.career.repository.CareerPathRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerPathService {

    private final CareerPathRepository careerPathRepository;

    @Transactional
    public CareerPathDTO createCareerPath(CreateCareerPathRequest request) {
        CareerPath careerPath = CareerPath.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .targetRole(request.getTargetRole())
                .difficultyLevel(request.getDifficultyLevel())
                .estimatedDurationWeeks(request.getEstimatedDurationWeeks())
                .requiredSkills(request.getRequiredSkills())
                .prerequisites(request.getPrerequisites())
                .isActive(true)
                .build();

        CareerPath saved = careerPathRepository.save(careerPath);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CareerPathDTO> getAllCareerPaths() {
        return careerPathRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CareerPathDTO getCareerPathById(Long id) {
        CareerPath careerPath = careerPathRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Career path no encontrado con ID: " + id));
        return mapToDTO(careerPath);
    }

    @Transactional(readOnly = true)
    public List<CareerPathDTO> getCareerPathsByRole(String targetRole) {
        return careerPathRepository.findByTargetRole(targetRole)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CareerPathDTO> getCareerPathsByDifficulty(String difficultyLevel) {
        return careerPathRepository.findByDifficultyLevel(difficultyLevel)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CareerPathDTO updateCareerPath(Long id, CreateCareerPathRequest request) {
        CareerPath careerPath = careerPathRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Career path no encontrado"));

        careerPath.setTitle(request.getTitle());
        careerPath.setDescription(request.getDescription());
        careerPath.setTargetRole(request.getTargetRole());
        careerPath.setDifficultyLevel(request.getDifficultyLevel());
        careerPath.setEstimatedDurationWeeks(request.getEstimatedDurationWeeks());
        careerPath.setRequiredSkills(request.getRequiredSkills());
        careerPath.setPrerequisites(request.getPrerequisites());

        CareerPath updated = careerPathRepository.save(careerPath);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteCareerPath(Long id) {
        CareerPath careerPath = careerPathRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Career path no encontrado"));
        careerPath.setIsActive(false);
        careerPathRepository.save(careerPath);
    }

    private CareerPathDTO mapToDTO(CareerPath careerPath) {
        return CareerPathDTO.builder()
                .id(careerPath.getId())
                .title(careerPath.getTitle())
                .description(careerPath.getDescription())
                .targetRole(careerPath.getTargetRole())
                .difficultyLevel(careerPath.getDifficultyLevel())
                .estimatedDurationWeeks(careerPath.getEstimatedDurationWeeks())
                .requiredSkills(careerPath.getRequiredSkills())
                .prerequisites(careerPath.getPrerequisites())
                .isActive(careerPath.getIsActive())
                .build();
    }
}
