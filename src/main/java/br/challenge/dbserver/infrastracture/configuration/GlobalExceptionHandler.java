package br.challenge.dbserver.infrastracture.configuration;

import br.challenge.dbserver.domain.adapters.service.NotFoundRulingException;
import br.challenge.dbserver.domain.adapters.service.ValidationRulingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ValidationRulingException.class, NotFoundRulingException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<String> handleValidationExceptions(final RuntimeException ex) {
        if (ex instanceof NotFoundRulingException notFoundException) {
            //spike: why ResponseEntity.notFound() is returning object type different from ResponseEntity.badRequest()
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body(notFoundException.getLocalizedMessage());
        }
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ex.getLocalizedMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(final MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.getBindingResult().getAllErrors()
                        .stream()
                        .filter(error -> error instanceof FieldError)
                        .collect(Collectors.toMap(
                                error -> ((FieldError) error).getField(),
                                ObjectError::getDefaultMessage)));
    }
}