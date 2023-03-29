package ru.practicum.shareit.item.repository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
//@RequiredArgsConstructor
@Data
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private Map<Long, Item> items = new HashMap<>();

    private long currentItemNumber = 0;

    //Метод добавления новой вещи в БД
    @Override
    public Item addItem(User user, Item item) {
        item.setId(getNextItemNumber());
        item.setOwner(user);
        items.put(item.getId(), item);
        log.info("Item with id {} was added.", items.get(item.getId()).getId());

        return items.get(getCurrentItemNumber());
    }

    //Метод обновления вещи
    @Override
    public Item updateItem(long userId, long itemId, Item item, boolean isDefinedAvailable) {
        item.setId(itemId);
        //В полях не переданных для обновления сохраняем прежние значения
        if (item.getName() == null) {
            item.setName(items.get(itemId).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(itemId).getDescription());
        }
        if (item.getOwner() == null) {
            item.setOwner(items.get(itemId).getOwner());
        }
        if (item.getRequest() == null) {
            item.setRequest(items.get(itemId).getRequest());
        }
        if (isDefinedAvailable) {
            item.setAvailable(item.isAvailable());
        } else {
            item.setAvailable(items.get(itemId).isAvailable());
        }

        items.replace(item.getId(), item);
        return items.get(item.getId());
    }

    //Метод получения списка вообще всех вещей
    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    //Метод получения вещи по ее id
    @Override
    public Item getById(long id) {
        return items.get(id);
    }

    //Метод получения списка вещей по id владельца
    @Override
    public List<Item> getItemsOfOwner(long userId) {
        return items.values()
                    .stream()
                    .filter(item -> item.getOwner().getId() == userId)
                    .collect(Collectors.toList());
    }

    //Метод получения списка вещей название или описание которых включает определенный текст
    @Override
    public List<Item> getItemsWithText(String searchText) {
        return items.values()
                .stream()
                .filter(item -> (item.getName().toUpperCase().contains(searchText.toUpperCase())
                                || item.getDescription().toUpperCase().contains(searchText.toUpperCase()))
                                && item.isAvailable())
                .collect(Collectors.toList());
    }

    //Служебный метод нумерации вещей
    private long getNextItemNumber() {
        return ++currentItemNumber;
    }
}
