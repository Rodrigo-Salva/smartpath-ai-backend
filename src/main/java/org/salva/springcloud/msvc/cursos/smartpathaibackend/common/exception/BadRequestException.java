package org.salva.springcloud.msvc.cursos.smartpathaibackend.common.exception;

// backend/src/main/java/com/smartpath/common/exception/BadRequestException.java

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
