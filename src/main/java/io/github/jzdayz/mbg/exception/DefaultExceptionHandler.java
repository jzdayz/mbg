package io.github.jzdayz.mbg.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@AllArgsConstructor
@RestControllerAdvice
@Slf4j
public class DefaultExceptionHandler {
    
    private ObjectMapper objectMapper;
    
    @ExceptionHandler(Exception.class)
    public Object defaultE(Exception ex) {
        log.error("error", ex);
        return ex;
    }
    
}
