package br.com.blade.indicafilme.exception;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

public class ApiError{
    @Getter
    @Setter
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String message, path;

    public ApiError(){}

    public ApiError(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
    }

}