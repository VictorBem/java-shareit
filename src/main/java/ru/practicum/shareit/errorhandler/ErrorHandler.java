package ru.practicum.shareit.errorhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityAlreadyExistException;

import java.util.NoSuchElementException;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse ErrorResponseNotFound(RuntimeException e) {
        log.error("Ошибка: " + e.getMessage());
        return new ErrorResponse("Ошибка.", e.getMessage());
    }

    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse ErrorResponseValidation(RuntimeException e) {
        log.error("Ошибка: " + e.getMessage());
        return new ErrorResponse("Ошибка.", e.getMessage());
    }

    @ExceptionHandler({EntityAlreadyExistException.class,
                       IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse ErrorResponseOtherException(RuntimeException e) {
        log.error("Ошибка: " + e.getMessage());
        return new ErrorResponse("Ошибка.", e.getMessage());
    }
}
