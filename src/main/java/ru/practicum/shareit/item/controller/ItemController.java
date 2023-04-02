package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;


@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    //Добавление вещи
    @PostMapping
    private ItemResponseDto addItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                            @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    //Обновление вещи
    @PatchMapping("/{itemId}")
    private ItemResponseDto update(@PathVariable("itemId") long itemId,
                                   @RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                   @RequestBody ItemDto item) {
        return itemService.updateItem(userId, itemId, item);
    }

    //Запрос вещи по ее id
    @GetMapping("/{id}")
    private ItemResponseDto getById(@PathVariable("id") int id) {
        return itemService.getById(id);
    }

    //Запрос по id владельца перечня всех вещей
    @GetMapping()
    private List<ItemResponseDto> getItemsOfOwner(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId) {
        return itemService.getItemsOfOwner(userId);
    }

    //Запрос доступных вещей по тексту в имени или описании
    @GetMapping("/search")
    private List<ItemResponseDto> searchItem(@RequestParam(value = "text", required = true) @NotBlank @NotEmpty String searchText) {
        return itemService.getItemsWithText(searchText);
    }
}
