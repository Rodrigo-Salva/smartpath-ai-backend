package org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.dto.*;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.entity.CV;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.entity.CVAnalysis;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.repository.CVRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.repository.CVAnalysisRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.ai.service.GeminiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CVService {

    private final CVRepository cvRepository;
    private final CVAnalysisRepository analysisRepository;
    private final UserRepository userRepository;
    private final CVTextExtractor textExtractor;
    private final GeminiService geminiService;

    @Value("${app.upload.dir:${user.home}/smartpath-uploads}")
    private String uploadDir;

    @Transactional
    public CVUploadResponse uploadCV(Long userId, MultipartFile file) throws IOException {
        // Validaciones
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        String contentType = file.getContentType();
        if (!"application/pdf".equals(contentType)) {
            throw new RuntimeException("Solo se permiten archivos PDF por ahora");
        }

        // Crear directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Guardar archivo
        Files.copy(file.getInputStream(), filePath);

        // Extraer texto
        String extractedText = textExtractor.extractTextFromPDF(file);

        // Buscar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear entidad CV
        CV cv = CV.builder()
                .user(user)
                .fileName(originalFilename)
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .fileType("PDF")
                .extractedText(extractedText)
                .isAnalyzed(false)
                .build();

        CV saved = cvRepository.save(cv);

        log.info("CV subido exitosamente: {}", saved.getId());

        return mapToUploadResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CVListDTO> getUserCVs(Long userId) {
        return cvRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToListDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CVUploadResponse getCVById(Long cvId, Long userId) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV no encontrado"));

        if (!cv.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para ver este CV");
        }

        return mapToUploadResponse(cv);
    }

    private CVUploadResponse mapToUploadResponse(CV cv) {
        return CVUploadResponse.builder()
                .id(cv.getId())
                .fileName(cv.getFileName())
                .fileSize(cv.getFileSize())
                .fileType(cv.getFileType())
                .isAnalyzed(cv.getIsAnalyzed())
                .createdAt(cv.getCreatedAt())
                .build();
    }

    private CVListDTO mapToListDTO(CV cv) {
        return CVListDTO.builder()
                .id(cv.getId())
                .fileName(cv.getFileName())
                .fileSize(cv.getFileSize())
                .isAnalyzed(cv.getIsAnalyzed())
                .createdAt(cv.getCreatedAt())
                .build();
    }
    @Transactional
    public void deleteCV(Long cvId, Long userId) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV no encontrado"));

        if (!cv.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para eliminar este CV");
        }

        // Eliminar archivo físico
        try {
            Path filePath = Paths.get(cv.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("No se pudo eliminar el archivo físico: {}", e.getMessage());
        }

        // Eliminar de BD
        cvRepository.delete(cv);
        log.info("CV {} eliminado", cvId);
    }

}

