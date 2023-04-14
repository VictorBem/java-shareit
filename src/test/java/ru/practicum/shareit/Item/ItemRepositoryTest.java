package ru.practicum.shareit.Item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;


@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    private Item testItem;
    private Item testItem1;
    private Item testItem2;

    //Сохранение item
    @Test
    void addNewItem() {
        prepareDataForTest();
        Item newItem = itemRepository.save(testItem);
        Assertions.assertEquals(testItem.getId(), newItem.getId(), "Вещи должны совпадать");
        Assertions.assertEquals(testItem.getName(), newItem.getName(), "Вещи должны совпадать");
        Assertions.assertEquals(testItem.getDescription(), newItem.getDescription(), "Вещи должны совпадать");
        Assertions.assertEquals(testItem.getOwner(), newItem.getOwner(), "Вещи должны совпадать");
        Assertions.assertEquals(testItem.getRequest(), newItem.getRequest(), "Вещи должны совпадать");
    }

    //Обновление item
    @Test
    void updateItem() {
        prepareDataForTest();
        Item newItem = itemRepository.save(testItem);
        newItem.setName("Updated in test");
        newItem.setDescription("Updated description in test");

        Item newItem1 = itemRepository.save(newItem);
        Assertions.assertEquals(newItem1.getId(), newItem.getId(), "Вещи должны совпадать");
        Assertions.assertEquals(newItem1.getName(), newItem.getName(), "Вещи должны совпадать");
        Assertions.assertEquals(newItem1.getDescription(), newItem.getDescription(), "Вещи должны совпадать");
        Assertions.assertEquals(newItem1.getOwner(), newItem.getOwner(), "Вещи должны совпадать");
        Assertions.assertEquals(newItem1.getRequest(), newItem.getRequest(), "Вещи должны совпадать");
    }

    //Поиск по тексту
    @Test
    void searchAvailableItemsByTextText() {
        prepareDataForTest();
        itemRepository.save(testItem1);
        itemRepository.save(testItem2);
        List<Item> resultOfSearch = itemRepository.searchAvailableItemsByText("2", PageRequest.of(0, 999, Sort.by("id").descending()));
        System.out.println(resultOfSearch.size());
        Assertions.assertEquals(resultOfSearch.get(0).getName(), testItem2.getName(), "Вещи должны совпадать");
        Assertions.assertEquals(resultOfSearch.get(0).getDescription(), testItem2.getDescription(), "Вещи должны совпадать");
        Assertions.assertEquals(resultOfSearch.get(0).getOwner(), testItem2.getOwner(), "Вещи должны совпадать");
        Assertions.assertEquals(resultOfSearch.get(0).getRequest(), testItem2.getRequest(), "Вещи должны совпадать");
    }

    private void prepareDataForTest() {
        //Создаем вещи для теста
        testItem = new Item();
        testItem.setId(1);
        testItem.setDescription("Test item");
        testItem.setName("Test item");
        testItem.setOwner(null);
        testItem.setAvailable(true);
        testItem.setRequest(null);

        testItem1 = new Item();
        testItem1.setId(2);
        testItem1.setDescription("Test item 1");
        testItem1.setName("Test item 1");
        testItem1.setOwner(null);
        testItem1.setAvailable(true);
        testItem1.setRequest(null);

        testItem2 = new Item();
        testItem2.setId(3);
        testItem2.setDescription("Test item 2");
        testItem2.setName("Test item 2");
        testItem2.setOwner(null);
        testItem2.setAvailable(true);
        testItem2.setRequest(null);
    }
}
