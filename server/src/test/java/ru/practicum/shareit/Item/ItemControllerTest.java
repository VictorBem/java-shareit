package ru.practicum.shareit.Item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private User testUser;
    private User testUser1;
    private Item testItem;
    private Request testRequest;
    private ItemResponseDto itemResponseDto;
    private ItemResponseDto itemResponseDto1;
    private List<ItemResponseDto> items;
    private ItemDto itemDto;
    private ItemDto itemDto1;
    private CommentDto commentDto;
    private CommentResponseDto commentResponseDto;
    @MockBean
    ItemService itemService;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    @Autowired
    private MockMvc mvc;

    //Создание новой вещи при корректных параметрах
    @Test
    void saveNewItemIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(itemService.addItem(any(Long.class), any(ItemDto.class))).thenReturn(itemResponseDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$.description").value(itemResponseDto.getDescription()))
                .andExpect(jsonPath("$.requestId").value(itemResponseDto.getRequestId()))
                .andExpect(jsonPath("$.description").value(itemResponseDto.getDescription()))
                .andExpect(jsonPath("$.owner.id").value(itemResponseDto.getOwner().getId()))
                .andExpect(jsonPath("$.owner.email").value(itemResponseDto.getOwner().getEmail()))
                .andExpect(jsonPath("$.owner.name").value(itemResponseDto.getOwner().getName()));
    }

    //Создание новой вещи при некорректном запросе
    @Test
    void saveNewItemIfBadRequest() throws Exception {
        when(itemService.addItem(any(Long.class), any(ItemDto.class))).thenThrow(BadRequestException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Создание новой вещи в случае отсутствия пользователя
    @Test
    void saveNewItemIfNoSuchElement() throws Exception {
        when(itemService.addItem(any(Long.class), any(ItemDto.class))).thenThrow(NoSuchElementException.class);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Обновление вещи
    @Test
    void updateItemIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(itemService.updateItem(any(Long.class), any(Long.class), any(ItemDto.class))).thenReturn(itemResponseDto1);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto1))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto1.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto1.getName()))
                .andExpect(jsonPath("$.description").value(itemResponseDto1.getDescription()))
                .andExpect(jsonPath("$.requestId").value(itemResponseDto1.getRequestId()))
                .andExpect(jsonPath("$.description").value(itemResponseDto1.getDescription()))
                .andExpect(jsonPath("$.owner.id").value(itemResponseDto1.getOwner().getId()))
                .andExpect(jsonPath("$.owner.email").value(itemResponseDto1.getOwner().getEmail()))
                .andExpect(jsonPath("$.owner.name").value(itemResponseDto1.getOwner().getName()));
    }

    //Обновление вещи - некорректный запрос
    @Test
    void updateItemIfBadRequest() throws Exception {
        prepareDataForTest();

        when(itemService.updateItem(any(Long.class), any(Long.class), any(ItemDto.class))).thenThrow(BadRequestException.class);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto1))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Получение вещи по ее id
    @Test
    void getByIdIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(itemService.getById(any(Long.class), any(Long.class))).thenReturn(itemResponseDto1);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto1.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto1.getName()))
                .andExpect(jsonPath("$.description").value(itemResponseDto1.getDescription()))
                .andExpect(jsonPath("$.requestId").value(itemResponseDto1.getRequestId()))
                .andExpect(jsonPath("$.description").value(itemResponseDto1.getDescription()))
                .andExpect(jsonPath("$.owner.id").value(itemResponseDto1.getOwner().getId()))
                .andExpect(jsonPath("$.owner.email").value(itemResponseDto1.getOwner().getEmail()))
                .andExpect(jsonPath("$.owner.name").value(itemResponseDto1.getOwner().getName()));
    }

    @Test
    void getByIdIfBadRequest() throws Exception {
        when(itemService.getById(any(Long.class), any(Long.class))).thenThrow(BadRequestException.class);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Получение списка вещей по id владельца
    @Test
    void getItemsOfOwnerIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(itemService.getItemsOfOwner(anyLong(), any(), any())).thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemResponseDto.getDescription()))
                .andExpect(jsonPath("$[0].requestId").value(itemResponseDto.getRequestId()))
                .andExpect(jsonPath("$[0].description").value(itemResponseDto.getDescription()))
                .andExpect(jsonPath("$[0].owner.id").value(itemResponseDto.getOwner().getId()))
                .andExpect(jsonPath("$[0].owner.email").value(itemResponseDto.getOwner().getEmail()))
                .andExpect(jsonPath("$[0].owner.name").value(itemResponseDto.getOwner().getName()))
                .andExpect(jsonPath("$[1].id").value(itemResponseDto1.getId()))
                .andExpect(jsonPath("$[1].name").value(itemResponseDto1.getName()))
                .andExpect(jsonPath("$[1].description").value(itemResponseDto1.getDescription()))
                .andExpect(jsonPath("$[1].requestId").value(itemResponseDto1.getRequestId()))
                .andExpect(jsonPath("$[1].description").value(itemResponseDto1.getDescription()))
                .andExpect(jsonPath("$[1].owner.id").value(itemResponseDto1.getOwner().getId()))
                .andExpect(jsonPath("$[1].owner.email").value(itemResponseDto1.getOwner().getEmail()))
                .andExpect(jsonPath("$[1].owner.name").value(itemResponseDto1.getOwner().getName()));
    }

    //Получение списка вещей по id владельца
    @Test
    void getItemsOfOwnerIfBadRequest() throws Exception {
        prepareDataForTest();

        when(itemService.getItemsOfOwner(anyLong(), any(), any())).thenThrow(BadRequestException.class);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Запрос доступных вещей по тексту в имени или описании
    @Test
    void getItemsWithTextIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(itemService.getItemsWithText(anyString(), any(), any())).thenReturn(items);

        mvc.perform(get("/items/search?text=Test")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemResponseDto.getDescription()))
                .andExpect(jsonPath("$[0].requestId").value(itemResponseDto.getRequestId()))
                .andExpect(jsonPath("$[0].description").value(itemResponseDto.getDescription()))
                .andExpect(jsonPath("$[0].owner.id").value(itemResponseDto.getOwner().getId()))
                .andExpect(jsonPath("$[0].owner.email").value(itemResponseDto.getOwner().getEmail()))
                .andExpect(jsonPath("$[0].owner.name").value(itemResponseDto.getOwner().getName()))
                .andExpect(jsonPath("$[1].id").value(itemResponseDto1.getId()))
                .andExpect(jsonPath("$[1].name").value(itemResponseDto1.getName()))
                .andExpect(jsonPath("$[1].description").value(itemResponseDto1.getDescription()))
                .andExpect(jsonPath("$[1].requestId").value(itemResponseDto1.getRequestId()))
                .andExpect(jsonPath("$[1].description").value(itemResponseDto1.getDescription()))
                .andExpect(jsonPath("$[1].owner.id").value(itemResponseDto1.getOwner().getId()))
                .andExpect(jsonPath("$[1].owner.email").value(itemResponseDto1.getOwner().getEmail()))
                .andExpect(jsonPath("$[1].owner.name").value(itemResponseDto1.getOwner().getName()));
    }

    //Запрос доступных вещей по тексту в имени или описании в случае ошибочного запроса
    @Test
    void getItemsWithTextIfBadRequest() throws Exception {
        prepareDataForTest();

        when(itemService.getItemsWithText(anyString(), any(), any())).thenThrow(BadRequestException.class);

        mvc.perform(get("/items/search?text=Test")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Добавления комментария
    @Test
    void addCommentIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(itemService.addComment(any(Long.class), any(Long.class), any(CommentDto.class))).thenReturn(commentResponseDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.text").value(commentResponseDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentResponseDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentResponseDto.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.item.id").value(commentResponseDto.getItem().getId()))
                .andExpect(jsonPath("$.item.description").value(commentResponseDto.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(commentResponseDto.getItem().isAvailable()))
                .andExpect(jsonPath("$.item.owner.id").value(commentResponseDto.getItem().getId()))
                .andExpect(jsonPath("$.item.owner.email").value(commentResponseDto.getItem().getOwner().getEmail()))
                .andExpect(jsonPath("$.item.owner.name").value(commentResponseDto.getItem().getOwner().getName()));
    }

    private void prepareDataForTest() {
        //Создаем пользователя для теста
        testUser = new User();
        testUser1 = new User();

        testUser.setId(1);
        testUser.setName("Test User");
        testUser.setEmail("testuser@ya.ru");

        testUser1.setId(2);
        testUser1.setName("Test User 1");
        testUser1.setEmail("testuser1@ya.ru");

        //Создаем запрос для теста
        testRequest = new Request();
        testRequest.setId(1);
        testRequest.setRequestor(testUser1);
        testRequest.setCreated(LocalDateTime.of(2023, 04, 11, 20, 23));
        testRequest.setDescription("I need test Item)))");

        //Создаем вещь для теста
        testItem = new Item();
        testItem.setId(1);
        testItem.setDescription("Test item");
        testItem.setName("Test item");
        testItem.setOwner(testUser);
        testItem.setAvailable(true);
        testItem.setRequest(testRequest);

        //DTO для запроса
        itemDto = new ItemDto(1,
                "Test ItemDto",
                "Description ItemDto",
                "true",
                testUser1,
                testRequest.getId());

        itemResponseDto = new ItemResponseDto(1,
                "Test ItemDto",
                "Description ItemDto",
                true,
                testUser1,
                testRequest.getId(),
                null,
                null,
                null);

        itemDto1 = new ItemDto(1,
                "Test ItemDto1",
                "Description ItemDto1",
                "false",
                testUser,
                testRequest.getId());

        itemResponseDto1 = new ItemResponseDto(1,
                "Test ItemDto1",
                "Description ItemDto1",
                false,
                testUser,
                testRequest.getId(),
                null,
                null,
                null);

        //Создаем DTO для комментария
        commentDto = new CommentDto();
        commentDto.setText("Test Comment 1");

        commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(1);
        commentResponseDto.setText("Test Comment 1");
        commentResponseDto.setItem(testItem);
        commentResponseDto.setCreated(LocalDateTime.of(2023,04,10, 22,00));
        commentResponseDto.setAuthorName("Test User");

        items = new LinkedList<>();
        items.add(itemResponseDto);
        items.add(itemResponseDto1);
    }

}
