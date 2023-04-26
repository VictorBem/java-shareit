package ru.practicum.shareit.errorhandler;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    //название ошибки
    private final String error;

    //Подробное описание
    private final String description;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }
}
