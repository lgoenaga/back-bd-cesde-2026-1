package com.cesde.studentinfo.exception;

/**
 * Excepción lanzada cuando un recurso no es encontrado (HTTP 404)
 * Utilizada en operaciones GET, PUT, DELETE cuando el ID no existe
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s not found with id: %d", resourceName, id));
    }

    public ResourceNotFoundException(String resourceName, String field, String value) {
        super(String.format("%s not found with %s: %s", resourceName, field, value));
    }
}

