package org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.service;

import lombok.RequiredArgsConstructor;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.dto.CreateResourceRequest;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.dto.LearningResourceDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.model.LearningResource;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.repository.LearningResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningResourceService {

    private final LearningResourceRepository resourceRepository;

    @Transactional
    public LearningResourceDTO createResource(CreateResourceRequest request) {
        LearningResource resource = LearningResource.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .resourceType(request.getResourceType())
                .provider(request.getProvider())
                .url(request.getUrl())
                .difficultyLevel(request.getDifficultyLevel())
                .estimatedHours(request.getEstimatedHours())
                .isFree(request.getIsFree())
                .price(request.getPrice())
                .rating(request.getRating())
                .tags(request.getTags())
                .isActive(true)
                .build();

        LearningResource saved = resourceRepository.save(resource);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<LearningResourceDTO> getAllResources() {
        return resourceRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LearningResourceDTO getResourceById(Long id) {
        LearningResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado con ID: " + id));
        return mapToDTO(resource);
    }

    @Transactional(readOnly = true)
    public List<LearningResourceDTO> getResourcesByType(String resourceType) {
        return resourceRepository.findByResourceType(resourceType)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LearningResourceDTO> getFreeResources() {
        return resourceRepository.findByIsFreeTrue()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LearningResourceDTO> getResourcesByTag(String tag) {
        return resourceRepository.findByTag(tag)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LearningResourceDTO updateResource(Long id, CreateResourceRequest request) {
        LearningResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setResourceType(request.getResourceType());
        resource.setProvider(request.getProvider());
        resource.setUrl(request.getUrl());
        resource.setDifficultyLevel(request.getDifficultyLevel());
        resource.setEstimatedHours(request.getEstimatedHours());
        resource.setIsFree(request.getIsFree());
        resource.setPrice(request.getPrice());
        resource.setRating(request.getRating());
        resource.setTags(request.getTags());

        LearningResource updated = resourceRepository.save(resource);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteResource(Long id) {
        LearningResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        resource.setIsActive(false);
        resourceRepository.save(resource);
    }

    private LearningResourceDTO mapToDTO(LearningResource resource) {
        return LearningResourceDTO.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .resourceType(resource.getResourceType())
                .provider(resource.getProvider())
                .url(resource.getUrl())
                .difficultyLevel(resource.getDifficultyLevel())
                .estimatedHours(resource.getEstimatedHours())
                .isFree(resource.getIsFree())
                .price(resource.getPrice())
                .rating(resource.getRating())
                .tags(resource.getTags())
                .build();
    }
}
