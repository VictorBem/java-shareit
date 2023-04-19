package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {
    private User testUser;
    private User testUser1;
    private RequestDto requestDto;
    private RequestDto requestDto1;
    private ResponseRequestDto responseRequestDto;
    private ResponseRequestDto responseRequestDto1;
    private List<ResponseRequestDto> requests;
    @MockBean
    RequestService requestService;
    @Autowired
    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    //Создание нового запроса
    @Test
    void saveNewUserIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(requestService.addRequest(anyLong(), any(RequestDto.class))).thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(requestDto.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.requestor.id").value(requestDto.getRequestor().getId()))
                .andExpect(jsonPath("$.requestor.name").value(requestDto.getRequestor().getName()))
                .andExpect(jsonPath("$.requestor.email").value(requestDto.getRequestor().getEmail()));
    }

    //Создание нового запроса - некорректный запрос
    @Test
    void saveNewUserIfBadRequest() throws Exception {
        prepareDataForTest();

        when(requestService.addRequest(anyLong(), any(RequestDto.class))).thenThrow(BadRequestException.class);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Поиск запросов по Id пользователя
    @Test
    void findAllRequestsByUserIdIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(requestService.findAllRequests(anyLong())).thenReturn(requests);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requests.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(requests.get(0).getDescription()))
                .andExpect(jsonPath("$[0].created").value(requests.get(0).getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[0].requestor.id").value(requests.get(0).getRequestor().getId()))
                .andExpect(jsonPath("$[0].requestor.name").value(requests.get(0).getRequestor().getName()))
                .andExpect(jsonPath("$[0].requestor.email").value(requests.get(0).getRequestor().getEmail()))
                .andExpect(jsonPath("$[1].id").value(requests.get(1).getId()))
                .andExpect(jsonPath("$[1].description").value(requests.get(1).getDescription()))
                .andExpect(jsonPath("$[1].created").value(requests.get(1).getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[1].requestor.id").value(requests.get(1).getRequestor().getId()))
                .andExpect(jsonPath("$[1].requestor.name").value(requests.get(1).getRequestor().getName()))
                .andExpect(jsonPath("$[1].requestor.email").value(requests.get(1).getRequestor().getEmail()));
    }

    //Поиск запросов по Id пользователя - некорректный запрос
    @Test
    void findAllRequestsByUserIdIfBadRequest() throws Exception {
        prepareDataForTest();

        when(requestService.findAllRequests(anyLong())).thenThrow(BadRequestException.class);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Получение запроса по Id
    @Test
    void findByIdIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(requestService.findById(anyLong(), anyLong())).thenReturn(responseRequestDto);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(responseRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").value(responseRequestDto.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.requestor.id").value(responseRequestDto.getRequestor().getId()))
                .andExpect(jsonPath("$.requestor.name").value(responseRequestDto.getRequestor().getName()))
                .andExpect(jsonPath("$.requestor.email").value(responseRequestDto.getRequestor().getEmail()));
    }

    //Получение запроса по Id - Некорректный запрос
    @Test
    void findByIdIfBadRequest() throws Exception {
        prepareDataForTest();

        when(requestService.findById(anyLong(), anyLong())).thenThrow(BadRequestException.class);

        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    //Поиск запросов по Id пользователя c группировкой по страницам
    @Test
    void findAllRequestsByPagesIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(requestService.findAllRequestsByPages(anyLong(), any(), any())).thenReturn(requests);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requests.get(0).getId()))
                .andExpect(jsonPath("$[0].description").value(requests.get(0).getDescription()))
                .andExpect(jsonPath("$[0].created").value(requests.get(0).getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[0].requestor.id").value(requests.get(0).getRequestor().getId()))
                .andExpect(jsonPath("$[0].requestor.name").value(requests.get(0).getRequestor().getName()))
                .andExpect(jsonPath("$[0].requestor.email").value(requests.get(0).getRequestor().getEmail()))
                .andExpect(jsonPath("$[1].id").value(requests.get(1).getId()))
                .andExpect(jsonPath("$[1].description").value(requests.get(1).getDescription()))
                .andExpect(jsonPath("$[1].created").value(requests.get(1).getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[1].requestor.id").value(requests.get(1).getRequestor().getId()))
                .andExpect(jsonPath("$[1].requestor.name").value(requests.get(1).getRequestor().getName()))
                .andExpect(jsonPath("$[1].requestor.email").value(requests.get(1).getRequestor().getEmail()));
    }

    //Поиск запросов по Id пользователя c группировкой по страницам - некорректный запрос
    @Test
    void findAllRequestsByPagesIfBadRequest() throws Exception {
        prepareDataForTest();

        when(requestService.findAllRequestsByPages(anyLong(), any(), any())).thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
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
        requestDto = new RequestDto(1, "Test Request", testUser,  LocalDateTime.of(2023,3,12,23,15));
        requestDto1 = new RequestDto(2, "TTest Request 1", testUser1, LocalDateTime.of(2023,4,9,11,5));
        responseRequestDto = new ResponseRequestDto(1,
                "Test request",
                testUser,
                LocalDateTime.of(2023,3,12,23,15),
                new ArrayList<>());

        responseRequestDto1 = new ResponseRequestDto(2,
                "Test request",
                testUser1,
                LocalDateTime.of(2023,4,9,11,5),
                new ArrayList<>());

        requests = new ArrayList<>();
        requests.add(responseRequestDto);
        requests.add(responseRequestDto1);
    }
}
