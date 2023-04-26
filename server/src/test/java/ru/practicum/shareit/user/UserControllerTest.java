package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    private User testUser;
    private User testUser1;
    private List<UserDto> users;
    private UserDto testUserDto;
    private UserDto testUserDto1;
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    //Получение всех пользователей, если запрос корректный
    @Test
    void getAllIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(userService.getAll()).thenReturn(users);

        mvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(users.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(users.get(0).getName()))
                .andExpect(jsonPath("$[0].email").value(users.get(0).getEmail()))
                .andExpect(jsonPath("$[1].id").value(users.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(users.get(1).getName()))
                .andExpect(jsonPath("$[1].email").value(users.get(1).getEmail()));
    }

    //Получение всех пользователей, если запрос некорреткный
    @Test
    void getAllIfBadRequest() throws Exception {
        prepareDataForTest();

        when(userService.getAll()).thenThrow(BadRequestException.class);

        mvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Создание нового пользователя
    @Test
    void saveNewUserIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(userService.addUser(any(UserDto.class))).thenReturn(testUserDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(testUserDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserDto.getId()))
                .andExpect(jsonPath("$.name").value(testUserDto.getName()))
                .andExpect(jsonPath("$.email").value(testUserDto.getEmail()));
    }

    //Создание нового пользователя - некорректный запрос
    @Test
    void saveNewUserIfBadRequest() throws Exception {
        prepareDataForTest();

        when(userService.addUser(any(UserDto.class))).thenThrow(BadRequestException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(testUserDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Обновление пользователя
    @Test
    void updateUserEverythingOk() throws Exception {
        prepareDataForTest();

        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(testUserDto1);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(testUserDto1))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserDto1.getId()))
                .andExpect(jsonPath("$.name").value(testUserDto1.getName()))
                .andExpect(jsonPath("$.email").value(testUserDto1.getEmail()));
    }

    //Обновление пользователя в случае некорректного запроса
    @Test
    void updateUserIfBadRequest() throws Exception {
        prepareDataForTest();

        when(userService.updateUser(anyLong(), any(UserDto.class))).thenThrow(BadRequestException.class);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(testUserDto1))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Получение пользователя по Id
    @Test
    void getByIdIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(userService.getById(anyLong())).thenReturn(testUserDto1);

        mvc.perform(get("/users/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserDto1.getId()))
                .andExpect(jsonPath("$.name").value(testUserDto1.getName()))
                .andExpect(jsonPath("$.email").value(testUserDto1.getEmail()));
    }

    //Получение пользователя по Id - некорректный запрос
    @Test
    void getByIdIfBadRequest() throws Exception {
        prepareDataForTest();

        when(userService.getById(anyLong())).thenThrow(BadRequestException.class);

        mvc.perform(get("/users/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Удаление пользователя пользователя по Id - некорректный запрос
    @Test
    void deleteByIdIfBadRequest() throws Exception {
        prepareDataForTest();

        mvc.perform(delete("/users/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
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

        //Создаем DTO для теста
        testUserDto = new UserDto(1, "Test User", "testuser@ya.ru");
        testUserDto1 = new UserDto(2, "Test User 1", "testuser1@ya.ru");


        users = new ArrayList<>();
        users.add(testUserDto);
        users.add(testUserDto1);
    }

}
