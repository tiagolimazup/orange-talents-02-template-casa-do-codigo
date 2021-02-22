package br.com.zup.bootcamp.casadocodigo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.util.Map.of;

@RestControllerAdvice
class UncaughtExceptionHandler {

    @ExceptionHandler
    ResponseEntity<?> onMethodArgumentException(MethodArgumentNotValidException e) {
        Collection<Map<String, String>> errors = new ArrayList<>();

        e.getBindingResult().getGlobalErrors()
                .stream()
                .map(o -> of("field", o.getObjectName(), "message", o.getDefaultMessage()))
                .forEach(errors::add);

        e.getBindingResult().getFieldErrors()
                .stream()
                .map(f -> of("field", f.getField(), "message", f.getDefaultMessage()))
                .forEach(errors::add);

        return ResponseEntity.badRequest()
                .body(of("errors", errors));
    }
}
