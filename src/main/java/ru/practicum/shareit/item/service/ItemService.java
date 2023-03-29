package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private static final boolean AVAILABLE_IS_NOT_DEFINED = false;
    private static final boolean AVAILABLE_IS_DEFINED =  true;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    //Метод добавления новой вещи в базу
    public ItemResponseDto addItem(long userId, ItemDto item) {
        //Если указан несуществующий пользователь или при создании новой вещи не указан статус ее доступности выбрасываем исключение
        if (userRepository.getAll().stream().noneMatch(u -> u.getId() == userId)) {
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        } else if (item.getAvailable() == null) {
            log.info("No initial available");
            throw new BadRequestException("No initial available");
        }
        //Определяем пользователя создающего вещь
        User currentUser = userRepository.getAll()
                                         .stream()
                                         .filter(u -> u.getId() == userId)
                                         .findFirst()
                                         .orElseThrow();

        //Вызываем метод создания новой вещи в базе данных
        Item createdItem = itemRepository.addItem(currentUser, ItemMapper.toItem(item));
        return ItemResponseMapper.toItemDto(createdItem);
    }

    public ItemResponseDto updateItem(long userId, long itemId, ItemDto item) {
        Item updatedItem;
        //Проверяем, что указанный пользователь и предмет существуют, а так же корректность владельца
        if (userRepository.getAll().stream().noneMatch(u -> u.getId() == userId)) {
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        } else if (itemRepository.getAll().stream().noneMatch(i -> i.getId() == itemId)) {
            log.info("Item with id: {} is not exist.", itemId);
            throw new NoSuchElementException("Item with id: " + itemId + " is not exist.");
        } else if (itemRepository.getAll().stream().noneMatch(i -> i.getId() == itemId && i.getOwner().getId() == userId)) {
            log.info("Item with id: {} has owner with another id.", itemId);
            throw new NoSuchElementException("Item with id: " + itemId + " has owner with another id.");
        } else if (item.getAvailable() == null || item.getAvailable().isBlank()) {
            updatedItem = itemRepository.updateItem(userId, itemId, ItemMapper.toItem(item), AVAILABLE_IS_NOT_DEFINED);
        } else {
            updatedItem = itemRepository.updateItem(userId, itemId, ItemMapper.toItem(item), AVAILABLE_IS_DEFINED);
        }
        return ItemResponseMapper.toItemDto(updatedItem);
    }


    //Получение вещи по id
    public ItemResponseDto getById (long itemId) {
        return ItemResponseMapper.toItemDto(itemRepository.getById(itemId));
    }

    //Получение списка вещей по id владельца
    public List<ItemResponseDto> getItemsOfOwner(long userId) {
        if (userRepository.getAll().stream().noneMatch(u -> u.getId() == userId)) {
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        }
        return itemRepository.getItemsOfOwner(userId).stream()
                                                     .map(ItemResponseMapper::toItemDto)
                                                     .collect(Collectors.toList());
    }

    //Получение списка доступных вещей по тексту входящему в название или описание
    public List<ItemResponseDto> getItemsWithText(String searchText) {
        if(searchText.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.getItemsWithText(searchText).stream()
                .map(ItemResponseMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
