package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.Request;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    //Метод из объекта модели создает DTO-объект
    public static RequestDto toItemRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor(),
                request.getCreated()
        );
    }

    //Метод из DTO-объекта создает объекта модели
    public static Request toItemRequest(RequestDto requestDto) {
        Request request = new Request();
        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setRequestor(requestDto.getRequestor());
        request.setCreated(requestDto.getCreated());
        return request;
    }
}
