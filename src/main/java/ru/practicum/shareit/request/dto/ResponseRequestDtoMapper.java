package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.Request;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseRequestDtoMapper {
    //Метод из объекта модели создает DTO-объект
    public static ResponseRequestDto toResponseRequestDto(Request request) {
        return new ResponseRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor(),
                request.getCreated(),
                null
        );
    }

    //Метод из DTO-объекта создает объекта модели
    public static Request toItemRequest(ResponseRequestDto requestDto) {
        Request request = new Request();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setRequestor(requestDto.getRequestor());
        request.setCreated(requestDto.getCreated());
        return request;
    }
}
