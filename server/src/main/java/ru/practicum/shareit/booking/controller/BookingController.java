package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnsupportedStatusException;


import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    //Добавление бронирования
    @PostMapping
    private BookingResponseDto addBooking(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                          @RequestBody BookingDto bookingDto) {
        log.info("Post request to add new booking");
        return bookingService.addBooking(userId, bookingDto);
    }

    //Обновление бронирования
    @PatchMapping("/{bookingId}")
    private BookingResponseDto update(@PathVariable("bookingId") long bookingId,
                                      @RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                      @RequestParam("approved") boolean approved) {
        log.info("Patch request for booking with id: {} .", bookingId);
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    //Получение бронирования по его Id
    @GetMapping("/{bookingId}")
    private BookingResponseDto findBookingByUserId(@PathVariable("bookingId") long bookingId,
                                                   @RequestHeader(value = "X-Sharer-User-Id", required = true) long userId) {
        log.info("Get request for booking with id: {} .", bookingId);
        return bookingService.findBookingByUserId(bookingId, userId);
    }

    //Получение бронирования по его статусу с разбиением по страницам
    @GetMapping()
    private List<BookingResponseDto> findAllBookingByUserId(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                                            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                                            @RequestParam(value = "from", required = false) Integer from,
                                                            @RequestParam(value = "size", required = false) Integer size) {
        BookingState stateParam = BookingState.from(state)
        		.orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        log.info("Get list of booking by state: {} .", state);
        return bookingService.findAllBookingByUserId(userId, stateParam, from, size);
    }

    //Все бронирования вещей владельца определенного по id
    @GetMapping("/owner")
    private List<BookingResponseDto> findAllBookingForAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                                               @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                                               @RequestParam(value = "from", required = false) Integer from,
                                                               @RequestParam(value = "size", required = false) Integer size) {
        log.info("Get list of booking by owner: {} .", state);
        BookingState stateParam = BookingState.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        return bookingService.findAllBookingForAllItems(userId, stateParam, from, size);
    }
}
