package org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.dto.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.entity.UserProgress;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.progress.repository.UserProgressRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.model.LearningResource;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.resource.repository.LearningResourceRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProgressService {

    private final UserProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final LearningResourceRepository resourceRepository;

    @Transactional
    public ProgressResponse createProgress(String email, CreateProgressRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LearningResource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        // Verificar si ya existe progreso
        progressRepository.findByUserIdAndResourceId(user.getId(), resource.getId())
                .ifPresent(p -> {
                    throw new RuntimeException("Ya existe progreso para este recurso");
                });

        UserProgress progress = UserProgress.builder()
                .user(user)
                .resource(resource)
                .progressPercentage(0)
                .status(UserProgress.ProgressStatus.NOT_STARTED)
                .timeSpentMinutes(0)
                .lastAccessedAt(LocalDateTime.now())
                .notes(request.getNotes())
                .build();

        UserProgress saved = progressRepository.save(progress);
        log.info("Progreso creado para usuario: {} en recurso: {}", email, resource.getTitle());

        return mapToResponse(saved);
    }

    @Transactional
    public ProgressResponse updateProgress(String email, Long progressId, UpdateProgressRequest request) {
        UserProgress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new RuntimeException("Progreso no encontrado"));

        // Verificar que el progreso pertenece al usuario
        if (!progress.getUser().getEmail().equals(email)) {
            throw new RuntimeException("No tienes permiso para actualizar este progreso");
        }

        if (request.getProgressPercentage() != null) {
            progress.setProgressPercentage(Math.min(request.getProgressPercentage(), 100));
        }

        if (request.getTimeSpentMinutes() != null) {
            Integer currentTime = progress.getTimeSpentMinutes();
            if (currentTime == null) {
                currentTime = 0;
            }
            progress.setTimeSpentMinutes(currentTime + request.getTimeSpentMinutes());
        }

        if (request.getStatus() != null) {
            try {
                progress.setStatus(UserProgress.ProgressStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Estado inválido. Valores permitidos: NOT_STARTED, IN_PROGRESS, COMPLETED, PAUSED");
            }
        }

        if (request.getNotes() != null) {
            progress.setNotes(request.getNotes());
        }

        progress.setLastAccessedAt(LocalDateTime.now());

        // Si llegó al 100%, marcar como completado
        if (progress.getProgressPercentage() >= 100) {
            progress.setStatus(UserProgress.ProgressStatus.COMPLETED);
            if (progress.getCompletedAt() == null) {
                progress.setCompletedAt(LocalDateTime.now());
            }
        }

        UserProgress updated = progressRepository.save(progress);
        log.info("Progreso actualizado: {}% para recurso: {}", updated.getProgressPercentage(),
                updated.getResource().getTitle());

        return mapToResponse(updated);
    }

    @Transactional
    public ProgressResponse completeProgress(String email, Long progressId) {
        UserProgress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new RuntimeException("Progreso no encontrado"));

        if (!progress.getUser().getEmail().equals(email)) {
            throw new RuntimeException("No tienes permiso para completar este progreso");
        }

        progress.setProgressPercentage(100);
        progress.setStatus(UserProgress.ProgressStatus.COMPLETED);
        progress.setCompletedAt(LocalDateTime.now());
        progress.setLastAccessedAt(LocalDateTime.now());

        UserProgress completed = progressRepository.save(progress);
        log.info("Recurso completado: {} por usuario: {}", completed.getResource().getTitle(), email);

        return mapToResponse(completed);
    }

    public List<ProgressResponse> getMyProgress(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return progressRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProgressStatsResponse getMyStats(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<UserProgress> allProgress = progressRepository.findByUserId(user.getId());
        Long completedCount = progressRepository.countCompletedByUserId(user.getId());
        Double avgProgress = progressRepository.getAverageProgressByUserId(user.getId());

        int totalTime = allProgress.stream()
                .mapToInt(p -> p.getTimeSpentMinutes() != null ? p.getTimeSpentMinutes() : 0)
                .sum();

        long inProgressCount = allProgress.stream()
                .filter(p -> p.getStatus() == UserProgress.ProgressStatus.IN_PROGRESS)
                .count();

        double completionRate = allProgress.isEmpty() ? 0.0 :
                (completedCount.doubleValue() / allProgress.size()) * 100;

        return ProgressStatsResponse.builder()
                .totalResources((long) allProgress.size())
                .completedResources(completedCount)
                .inProgressResources(inProgressCount)
                .averageProgress(avgProgress != null ? avgProgress : 0.0)
                .totalTimeSpentMinutes(totalTime)
                .completionRate(completionRate)
                .build();
    }

    private ProgressResponse mapToResponse(UserProgress progress) {
        return ProgressResponse.builder()
                .id(progress.getId())
                .userId(progress.getUser().getId())
                .resourceId(progress.getResource().getId())
                .resourceTitle(progress.getResource().getTitle())
                .resourceType(progress.getResource().getResourceType().toString())
                .progressPercentage(progress.getProgressPercentage())
                .status(progress.getStatus().toString())
                .timeSpentMinutes(progress.getTimeSpentMinutes())
                .lastAccessedAt(progress.getLastAccessedAt())
                .completedAt(progress.getCompletedAt())
                .notes(progress.getNotes())
                .createdAt(progress.getCreatedAt())
                .build();
    }
}
