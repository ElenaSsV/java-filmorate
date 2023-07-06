package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        final List<InputError> inputErrors = e.getConstraintViolations().stream()
                .map(inputError -> new InputError(inputError.getPropertyPath().toString(), inputError.getMessage()))
                .collect(Collectors.toList());
        log.info("Input validation error: {}", inputErrors);
        return new ValidationErrorResponse(inputErrors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<InputError> inputErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new InputError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.info("Input validation error: {}", inputErrors);
        return new ValidationErrorResponse(inputErrors);
    }
}
