package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShortItemDtoMapper {

    //Метод из объекта модели создает DTO-объект
    public static ShortItemDto toShortItemDto(Item item) {
        return new ShortItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner().getId(),
                item.getRequest().getId()
        );
    }

    //Метод из DTO-объекта создает объекта модели
    public static Item toItem(ShortItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.isAvailable());
        item.setOwner(null);
        item.setRequest(null);
        return item;
    }
}
