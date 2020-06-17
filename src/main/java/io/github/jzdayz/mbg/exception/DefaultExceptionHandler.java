package io.github.jzdayz.mbg.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@AllArgsConstructor
@RestControllerAdvice
public class DefaultExceptionHandler {

    private ObjectMapper objectMapper;

    @ExceptionHandler(Exception.class)
    public Object defaultE(Exception ex) {
        return ex;
    }

}
