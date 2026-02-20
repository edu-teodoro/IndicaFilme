// Adicionando conteúdo da exceção NotFoundException
package com.example.blackflix.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
