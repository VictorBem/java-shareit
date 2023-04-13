package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    private final static long NO_ANY_REQUEST_FOR_ITEM = -1;
    //Метод из объекта модели создает DTO-объект
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable() ? "true" : "false",
                item.getOwner(),
                item.getRequest() != null ? item.getRequest().getId() : NO_ANY_REQUEST_FOR_ITEM
        );

    }

    //Метод из DTO-объекта создает объекта модели
    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable() != null && (itemDto.getAvailable().equals("true")));
        item.setOwner(itemDto.getOwner());
        item.setRequest(null);
        return item;
    }

}
