package com.labGCL03.moeda_estudantil.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s com ID %d não encontrado", resource, id));
    }
}
