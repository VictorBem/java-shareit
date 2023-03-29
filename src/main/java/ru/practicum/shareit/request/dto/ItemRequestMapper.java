package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestMapper {

    //Метод из объекта модели создает DTO-объект
    public static ItemRequestDto toUserDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated()
        );
    }

    //Метод из DTO-объекта создает объекта модели
    public static ItemRequest toUser(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequestor(),
                itemRequestDto.getCreated()
        );
    }
}
