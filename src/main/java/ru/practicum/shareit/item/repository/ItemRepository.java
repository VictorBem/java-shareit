package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {
    //Метод добавления новой вещи в БД
    Item addItem(User user, Item item);

    //Метод получения списка вообще всех вещей
    List<Item> getAll();

    //Метод получения вещи по ее id
    Item getById(long id);

    //Метод обновления вещи
    Item updateItem(long userId, long itemId, Item item, boolean isDefinedAvailable);

    //Метод получения списка вещей по id владельца
    List<Item> getItemsOfOwner(long userId);

    //Метод получения списка вещей название или описание которых включает определенный текст
    List<Item> getItemsWithText(String searchText);

}
