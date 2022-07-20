package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlers {

    @ExceptionHandler
    public ResponseEntity<String> handleAlreadyExistException(final AlreadyExistException e) {
        log.error("500 {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleValidationException(final ValidationException e) {
        log.error("400 {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleNotFoundException(final NotFoundException e) {
        log.error("404 {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
