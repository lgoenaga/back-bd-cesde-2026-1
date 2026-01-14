package com.cesde.studentinfo.exception;

/**
 * Excepción para errores de acceso a datos (HTTP 500)
 * Se lanza cuando hay problemas con repositorios o base de datos
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }
}

