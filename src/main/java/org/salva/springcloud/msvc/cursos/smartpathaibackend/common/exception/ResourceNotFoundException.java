package org.salva.springcloud.msvc.cursos.smartpathaibackend.common.exception;

// backend/src/main/java/com/smartpath/common/exception/ResourceNotFoundException.java

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s no encontrado con %s: '%s'", resource, field, value));
    }
}

