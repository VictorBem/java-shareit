package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Slf4j
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
                                    @RequestBody ItemDto itemDto) {
        log.info("Post request for new Item by user with id {} ", userId);
        return itemService.addItem(userId, itemDto);
    }

    //Обновление вещи
    @PatchMapping("/{itemId}")
    private ItemResponseDto update(@PathVariable("itemId") long itemId,
                                   @RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                   @RequestBody ItemDto item) {
        log.info("Patch request to update item with id {} ", itemId);
        return itemService.updateItem(userId, itemId, item);
    }

    //Запрос вещи по ее id
    @GetMapping("/{id}")
    private ItemResponseDto getById(@PathVariable("id") long itemId,
                                    @RequestHeader(value = "X-Sharer-User-Id", required = true) long userId) {
        log.info("Get request to receive item with id {} ", itemId);
        return itemService.getById(userId, itemId);
    }

    //Запрос по id владельца перечня всех вещей
    @GetMapping()
    private List<ItemResponseDto> getItemsOfOwner(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                                  @RequestParam(value = "from", required = false) Integer from,
                                                  @RequestParam(value = "size", required = false) Integer size) {
        log.info("Get list of Items by owner with id {} ", userId);
        return itemService.getItemsOfOwner(userId, from, size);
    }

    //Запрос доступных вещей по тексту в имени или описании
    @GetMapping("/search")
    private List<ItemResponseDto> searchItem(@RequestParam(value = "text", required = true) @NotBlank String searchText,
                                             @RequestParam(value = "from", required = false) Integer from,
                                             @RequestParam(value = "size", required = false) Integer size) {
        log.info("Get list of Items by string {} un name or description", searchText);
        return itemService.getItemsWithText(searchText, from, size);
    }

    //Добавление комментария
    @PostMapping("/{itemId}/comment")
    private CommentResponseDto addComment(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                          @PathVariable("itemId") long itemId,
                                          @RequestBody CommentDto commentDto) {
        log.info("Add comment to item with id: {} by user with id {}", itemId, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
