package org.salva.springcloud.msvc.cursos.smartpathaibackend.common.exception;

// backend/src/main/java/com/smartpath/common/exception/UnauthorizedException.java

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

