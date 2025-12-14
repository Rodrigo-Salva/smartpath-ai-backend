package org.salva.springcloud.msvc.cursos.smartpathaibackend.common.util;

// backend/src/main/java/com/smartpath/common/util/FileUtils.java

import org.salva.springcloud.msvc.cursos.smartpathaibackend.common.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_TYPES = {"application/pdf"};

    public static void validatePdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo no puede estar vacío");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException(
                    "El archivo es demasiado grande. Máximo 5MB permitido"
            );
        }

        String contentType = file.getContentType();
        boolean isValidType = false;
        for (String type : ALLOWED_TYPES) {
            if (type.equals(contentType)) {
                isValidType = true;
                break;
            }
        }

        if (!isValidType) {
            throw new BadRequestException(
                    "Tipo de archivo no válido. Solo se permiten archivos PDF"
            );
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new BadRequestException("El archivo debe tener extensión .pdf");
        }
    }

    public static String sanitizeFilename(String filename) {
        if (filename == null) return "unnamed.pdf";
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}
