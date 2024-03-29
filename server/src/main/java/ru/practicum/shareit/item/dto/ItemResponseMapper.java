package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemResponseMapper {
    private final static long NO_ANY_REQUEST_FOR_ITEM = -1;
    //Метод из объекта модели создает DTO-объект
    public static ItemResponseDto toItemDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner(),
                item.getRequest() != null ? item.getRequest().getId() : NO_ANY_REQUEST_FOR_ITEM,
                null,
                null,
                new ArrayList<CommentResponseDto>()
        );
    }

    //Метод из DTO-объекта создает объекта модели
    public static Item toItem(ItemResponseDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.isAvailable());
        item.setOwner(itemDto.getOwner());
        item.setRequest(null);
        return item;
    }
}
