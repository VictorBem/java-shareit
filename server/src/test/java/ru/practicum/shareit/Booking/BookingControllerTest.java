package ru.practicum.shareit.Booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    private User testUser;
    private User testUser1;
    private User testUser2;
    private Item testItem;
    private Request testRequest;
    private BookingResponseDto bookingResponseDto;
    private BookingResponseDto bookingResponseDto1;
    private List<BookingResponseDto> bookings;
    private BookingDto bookingDto;
    private BookingDto bookingDto1;
    @MockBean
    BookingService bookingService;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
           .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    @Autowired
    private MockMvc mvc;

    //Тест создания пользователя, если все данные запроса корректны
    @Test
    void saveNewUserIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(bookingService.addBooking(any(Long.class), any(BookingDto.class))).thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    //Тест создания пользователя, в случае получения некорректного запроса
    @Test
    void saveNewUserIfWrongRequest() throws Exception {

        when(bookingService.addBooking(any(Long.class), any(BookingDto.class))).thenThrow(BadRequestException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 99)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Вызов метода создания вещи в случае указания не корректного пользователя или вещи
    @Test
    void saveNewUserIfNoSuchElement() throws Exception {

        when(bookingService.addBooking(any(Long.class), any(BookingDto.class))).thenThrow(NoSuchElementException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Обновление бронирования при корректных параметрах
    @Test
    void updateBookingIsEverythingIsOK() throws Exception {
        prepareDataForTest();
        when(bookingService.updateBooking(any(Long.class), any(Long.class), anyBoolean())).thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getItemId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    //Обновление бронирования в случае получения некорректного запроса
    @Test
    void updateBookingIfBadRequest() throws Exception {
        prepareDataForTest();
        when(bookingService.updateBooking(any(Long.class), any(Long.class), anyBoolean())).thenThrow(BadRequestException.class);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Обновление бронирования в случае передачи некорректного id клиента или вещи
    @Test
    void updateBookingIfNoSuchElement() throws Exception {
        prepareDataForTest();
        when(bookingService.updateBooking(any(Long.class), any(Long.class), anyBoolean())).thenThrow(NoSuchElementException.class);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Получение бронирования по id если все параметры указаны корректно
    @Test
    void getBookingByIdIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(bookingService.findAllBookingByUserId(anyLong(), any(), any(), any())).thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookings.get(0).getId()))
                .andExpect(jsonPath("$[0].status").value(bookings.get(0).getStatus().toString()))
                .andExpect(jsonPath("$[0].start").value(bookings.get(0).getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[0].end").value(bookings.get(0).getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[0].item.id").value(bookings.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookings.get(0).getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(bookings.get(0).getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(bookings.get(0).getItem().isAvailable()))
                .andExpect(jsonPath("$[0].item.owner.id").value(bookings.get(0).getItem().getOwner().getId()))
                .andExpect(jsonPath("$[0].item.owner.name").value(bookings.get(0).getItem().getOwner().getName()))
                .andExpect(jsonPath("$[0].item.owner.email").value(bookings.get(0).getItem().getOwner().getEmail()))
                .andExpect(jsonPath("$[1].id").value(bookings.get(1).getId()))
                .andExpect(jsonPath("$[1].status").value(bookings.get(1).getStatus().toString()))
                .andExpect(jsonPath("$[1].start").value(bookings.get(1).getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[1].end").value(bookings.get(1).getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[1].item.id").value(bookings.get(1).getItem().getId()))
                .andExpect(jsonPath("$[1].item.name").value(bookings.get(1).getItem().getName()))
                .andExpect(jsonPath("$[1].item.description").value(bookings.get(1).getItem().getDescription()))
                .andExpect(jsonPath("$[1].item.available").value(bookings.get(1).getItem().isAvailable()))
                .andExpect(jsonPath("$[1].item.owner.id").value(bookings.get(1).getItem().getOwner().getId()))
                .andExpect(jsonPath("$[1].item.owner.name").value(bookings.get(1).getItem().getOwner().getName()))
                .andExpect(jsonPath("$[1].item.owner.email").value(bookings.get(1).getItem().getOwner().getEmail()));
    }

    //Получение бронирования по id если получен некорректный запрос
    @Test
    void getBookingByIdIfBadRequest() throws Exception {

        when(bookingService.findAllBookingByUserId(anyLong(), any(), any(), any())).thenThrow(BadRequestException.class);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Получение бронирования по id если в случае некорректного указания пользователя, вещи и т.д.
    @Test
    void getBookingByIdIfServerError() throws Exception {

        when(bookingService.findAllBookingByUserId(anyLong(), any(), any(), any())).thenThrow(NoSuchElementException.class);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Получение бронирования по всем вещам владельца если все параметры указаны корректно
    @Test
    void getBookingByOwnerIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(bookingService.findAllBookingForAllItems(anyLong(), any(), any(), any())).thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookings.get(0).getId()))
                .andExpect(jsonPath("$[0].status").value(bookings.get(0).getStatus().toString()))
                .andExpect(jsonPath("$[0].start").value(bookings.get(0).getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[0].end").value(bookings.get(0).getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[0].item.id").value(bookings.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookings.get(0).getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(bookings.get(0).getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(bookings.get(0).getItem().isAvailable()))
                .andExpect(jsonPath("$[0].item.owner.id").value(bookings.get(0).getItem().getOwner().getId()))
                .andExpect(jsonPath("$[0].item.owner.name").value(bookings.get(0).getItem().getOwner().getName()))
                .andExpect(jsonPath("$[0].item.owner.email").value(bookings.get(0).getItem().getOwner().getEmail()))
                .andExpect(jsonPath("$[1].id").value(bookings.get(1).getId()))
                .andExpect(jsonPath("$[1].status").value(bookings.get(1).getStatus().toString()))
                .andExpect(jsonPath("$[1].start").value(bookings.get(1).getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[1].end").value(bookings.get(1).getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[1].item.id").value(bookings.get(1).getItem().getId()))
                .andExpect(jsonPath("$[1].item.name").value(bookings.get(1).getItem().getName()))
                .andExpect(jsonPath("$[1].item.description").value(bookings.get(1).getItem().getDescription()))
                .andExpect(jsonPath("$[1].item.available").value(bookings.get(1).getItem().isAvailable()))
                .andExpect(jsonPath("$[1].item.owner.id").value(bookings.get(1).getItem().getOwner().getId()))
                .andExpect(jsonPath("$[1].item.owner.name").value(bookings.get(1).getItem().getOwner().getName()))
                .andExpect(jsonPath("$[1].item.owner.email").value(bookings.get(1).getItem().getOwner().getEmail()));
    }

    //Получение бронирования по id в случае получения некорректного запроса
    @Test
    void getBookingByOwnerIfBadRequest() throws Exception {

        when(bookingService.findAllBookingForAllItems(anyLong(), any(), any(), any())).thenThrow(BadRequestException.class);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    //Получение бронирований владельца в случае некорректного указания пользователя, вещи и т.д.
    @Test
    void getBookingByOwnerIfNoSuchElement() throws Exception {

        when(bookingService.findAllBookingForAllItems(anyLong(), any(), any(), any())).thenThrow(UnsupportedStatusException.class);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findBookingByIdIfEverythingOk() throws Exception {
        prepareDataForTest();

        when(bookingService.findBookingByUserId(anyLong(), anyLong())).thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$.start").value(bookingResponseDto.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.end").value(bookingResponseDto.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.item.id").value(bookingResponseDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingResponseDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingResponseDto.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingResponseDto.getItem().isAvailable()))
                .andExpect(jsonPath("$.item.owner.id").value(bookingResponseDto.getItem().getOwner().getId()))
                .andExpect(jsonPath("$.item.owner.name").value(bookingResponseDto.getItem().getOwner().getName()))
                .andExpect(jsonPath("$.item.owner.email").value(bookingResponseDto.getItem().getOwner().getEmail()));

    }


    private void prepareDataForTest() {
        //Создаем пользователя для теста
        testUser = new User();
        testUser1 = new User();
        testUser2 = new User();

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
        testRequest.setCreated(LocalDateTime.of(2023,04,11,20,23));
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
        bookingResponseDto = new BookingResponseDto(1,
                LocalDateTime.of(2023,04,11,20,23),
                LocalDateTime.of(2023,05,12,20,23),
                testItem,
                testUser1,
                StatusOfBooking.APPROVED);

        bookingResponseDto1 = new BookingResponseDto(2,
                LocalDateTime.of(2022,01,30,11,45),
                LocalDateTime.of(2022,01,31,22,17),
                testItem,
                testUser1,
                StatusOfBooking.APPROVED);

        bookingDto = new BookingDto(1,
                LocalDateTime.of(2023,04,11,20,23),
                LocalDateTime.of(2023,05,12,20,23),
                testItem,
                testUser1,
                StatusOfBooking.APPROVED);

        bookingDto1 = new BookingDto(2,
                LocalDateTime.of(2022,01,30,11,45),
                LocalDateTime.of(2022,01,31,22,17),
                testItem,
                testUser1,
                StatusOfBooking.APPROVED);

        bookings = new LinkedList<>();
        bookings.add(bookingResponseDto);
        bookings.add(bookingResponseDto1);
    }

}
