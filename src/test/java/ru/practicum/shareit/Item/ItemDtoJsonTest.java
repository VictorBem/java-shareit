package ru.practicum.shareit.Item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
        User testUser = new User();
        testUser.setId(1);
        testUser.setName("Test User");
        testUser.setEmail("testuser@ya.ru");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setAvailable("true");
        itemDto.setName("Test item");
        itemDto.setDescription("Test description");

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test description");
        assertThat(result).extractingJsonPathStringValue("$.available").isEqualTo("true");
    }
}
