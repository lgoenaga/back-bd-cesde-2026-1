package com.cesde.studentinfo.exception;

/**
 * Excepción para errores de lógica de negocio (HTTP 400)
 * Utilizada cuando se viola una regla de negocio de la aplicación
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

