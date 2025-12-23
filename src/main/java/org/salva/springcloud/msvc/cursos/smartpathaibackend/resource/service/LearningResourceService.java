package org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.dto.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.model.LearningResource;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.repository.LearningResourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningResourceService {

    private final LearningResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public List<LearningResourceDTO> getAllResources(ResourceFilterDTO filter, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 50);

        List<LearningResource> resources;

        // Aplicar filtros
        if (filter != null) {
            resources = applyFilters(filter);
        } else {
            resources = resourceRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        }

        return resources.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LearningResourceDTO getResourceById(Long id) {
        LearningResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        return mapToDTO(resource);
    }

    @Transactional(readOnly = true)
    public List<LearningResourceDTO> getResourcesByType(String type) {
        return resourceRepository.findByResourceTypeAndIsActiveTrue(type)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LearningResourceDTO> getResourcesByDifficulty(String difficulty) {
        return resourceRepository.findByDifficultyLevelAndIsActiveTrue(difficulty)
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
    public List<LearningResourceDTO> searchResources(String query) {
        return resourceRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LearningResourceDTO createResource(CreateResourceRequest request) {
        // Validar URL única
        if (resourceRepository.findByUrl(request.getUrl()).isPresent()) {
            throw new RuntimeException("Ya existe un recurso con esta URL");
        }

        LearningResource resource = LearningResource.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .url(request.getUrl())
                .resourceType(request.getResourceType())
                .difficultyLevel(request.getDifficultyLevel())
                .provider(request.getProvider())
                .estimatedHours(request.getEstimatedHours())
                .isFree(request.getIsFree())
                .price(request.getPrice())
                .rating(request.getRating())
                .tags(request.getTags())
                .isActive(true)
                .build();

        LearningResource saved = resourceRepository.save(resource);
        log.info("Recurso creado: {}", saved.getTitle());

        return mapToDTO(saved);
    }

    @Transactional
    public LearningResourceDTO updateResource(Long id, UpdateResourceRequest request) {
        LearningResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        // Actualizar campos
        if (request.getTitle() != null) resource.setTitle(request.getTitle());
        if (request.getDescription() != null) resource.setDescription(request.getDescription());
        if (request.getUrl() != null) resource.setUrl(request.getUrl());
        if (request.getResourceType() != null) resource.setResourceType(request.getResourceType());
        if (request.getDifficultyLevel() != null) resource.setDifficultyLevel(request.getDifficultyLevel());
        if (request.getProvider() != null) resource.setProvider(request.getProvider());
        if (request.getEstimatedHours() != null) resource.setEstimatedHours(request.getEstimatedHours());
        if (request.getIsFree() != null) resource.setIsFree(request.getIsFree());
        if (request.getPrice() != null) resource.setPrice(request.getPrice());
        if (request.getRating() != null) resource.setRating(request.getRating());
        if (request.getTags() != null) resource.setTags(request.getTags());
        if (request.getIsActive() != null) resource.setIsActive(request.getIsActive());

        LearningResource updated = resourceRepository.save(resource);
        log.info("Recurso actualizado: {}", updated.getId());

        return mapToDTO(updated);
    }

    @Transactional
    public void deleteResource(Long id) {
        LearningResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        // Soft delete
        resource.setIsActive(false);
        resourceRepository.save(resource);

        log.info("Recurso desactivado: {}", id);
    }

    @Transactional
    public void permanentDeleteResource(Long id) {
        resourceRepository.deleteById(id);
        log.info("Recurso eliminado permanentemente: {}", id);
    }

    @Transactional(readOnly = true)
    public ResourceStatsDTO getResourceStats() {
        List<LearningResource> all = resourceRepository.findAll();

        long total = all.size();
        long freeCount = all.stream().filter(r -> Boolean.TRUE.equals(r.getIsFree())).count();
        long paidCount = total - freeCount;

        long courses = all.stream().filter(r -> "COURSE".equals(r.getResourceType())).count();
        long books = all.stream().filter(r -> "BOOK".equals(r.getResourceType())).count();
        long videos = all.stream().filter(r -> "VIDEO".equals(r.getResourceType())).count();

        double avgHours = all.stream()
                .filter(r -> r.getEstimatedHours() != null)
                .mapToInt(LearningResource::getEstimatedHours)
                .average()
                .orElse(0.0);

        List<String> topProviders = all.stream()
                .map(LearningResource::getProvider)
                .filter(p -> p != null)
                .distinct()
                .limit(5)
                .collect(Collectors.toList());

        return ResourceStatsDTO.builder()
                .totalResources(total)
                .freeResources(freeCount)
                .paidResources(paidCount)
                .courseCount(courses)
                .bookCount(books)
                .videoCount(videos)
                .avgEstimatedHours(avgHours)
                .topProviders(topProviders)
                .build();
    }

    private List<LearningResource> applyFilters(ResourceFilterDTO filter) {
        List<LearningResource> resources = resourceRepository.findByIsActiveTrueOrderByCreatedAtDesc();

        return resources.stream()
                .filter(r -> filter.getResourceType() == null || filter.getResourceType().equals(r.getResourceType()))
                .filter(r -> filter.getDifficultyLevel() == null || filter.getDifficultyLevel().equals(r.getDifficultyLevel()))
                .filter(r -> filter.getProvider() == null || filter.getProvider().equalsIgnoreCase(r.getProvider()))
                .filter(r -> filter.getIsFree() == null || filter.getIsFree().equals(r.getIsFree()))
                .filter(r -> filter.getTag() == null || (r.getTags() != null && r.getTags().contains(filter.getTag())))
                .filter(r -> filter.getMinHours() == null || (r.getEstimatedHours() != null && r.getEstimatedHours() >= filter.getMinHours()))
                .filter(r -> filter.getMaxHours() == null || (r.getEstimatedHours() != null && r.getEstimatedHours() <= filter.getMaxHours()))
                .collect(Collectors.toList());
    }

    private LearningResourceDTO mapToDTO(LearningResource resource) {
        return LearningResourceDTO.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .url(resource.getUrl())
                .resourceType(resource.getResourceType())
                .difficultyLevel(resource.getDifficultyLevel())
                .provider(resource.getProvider())
                .estimatedHours(resource.getEstimatedHours())
                .isFree(resource.getIsFree())
                .price(resource.getPrice())
                .rating(resource.getRating())
                .tags(resource.getTags())
                .isActive(resource.getIsActive())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }
}
