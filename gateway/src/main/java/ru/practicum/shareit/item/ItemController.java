package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    //Добавление вещи
    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                          @RequestBody ItemDto itemDto) {
        log.info("Post request for new Item by user with id {} ", userId);
        return itemClient.addItem(userId, itemDto);
    }

    //Обновление вещи
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable("itemId") long itemId,
                                         @RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                         @RequestBody ItemDto item) {
        log.info("Patch request to update item with id {} ", itemId);
        return itemClient.updateItem(userId, itemId, item);
    }

    //Запрос вещи по ее id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") long itemId,
                                    @RequestHeader(value = "X-Sharer-User-Id", required = true) long userId) {
        log.info("Get request to receive item with id {} ", itemId);
        return itemClient.getById(userId, itemId);
    }

    //Запрос по id владельца перечня всех вещей
    @GetMapping()
    public ResponseEntity<Object> getItemsOfOwner(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                                  @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", required = false, defaultValue = "99") Integer size) {
        log.info("Get list of Items by owner with id {} ", userId);
        return itemClient.getItemsOfOwner(userId, from, size);
    }

    //Запрос доступных вещей по тексту в имени или описании
    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                             @RequestParam(value = "text", required = true) String searchText,
                                             @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                             @RequestParam(value = "size", required = false, defaultValue = "99") Integer size) {
        log.info("Get list of Items by string {} un name or description", searchText);
        return itemClient.getItemsWithText(searchText, userId, from, size);
    }

    //Добавление комментария
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                             @PathVariable("itemId") long itemId,
                                             @RequestBody CommentDto commentDto) {
        log.info("Add comment to item with id: {} by user with id {}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
