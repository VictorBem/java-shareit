package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemResponseMapper {
    //Метод из объекта модели создает DTO-объект
    public static ItemResponseDto toItemDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner(),
                item.getRequest() != null ? item.getRequest() : null
        );
    }

    //Метод из DTO-объекта создает объекта модели
    public static Item toItem(ItemResponseDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.isAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest() != null ? itemDto.getRequest() : null
        );
    }
}
