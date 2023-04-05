package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;


import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    //Добавление бронирования
    @PostMapping
    private BookingResponseDto addItem(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                       @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    //Обновление бронирования
    @PatchMapping("/{bookingId}")
    private BookingResponseDto update(@PathVariable("bookingId") long bookingId,
                                      @RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                      @RequestParam("approved") boolean approved) {

       return bookingService.updateBooking(bookingId, userId, approved);
    }

    //Получение бронирования по его Id
    @GetMapping("/{bookingId}")
    private BookingResponseDto findBookingByUserId(@PathVariable("bookingId") long bookingId,
                                                   @RequestHeader(value = "X-Sharer-User-Id", required = true) long userId) {

        return bookingService.findBookingByUserId(bookingId, userId);
    }

    //Получение бронирования по его Id
    @GetMapping()
    private List<BookingResponseDto> findAllBookingByUserId(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                                            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {

        return bookingService.findAllBookingByUserId(userId, state);
    }

    @GetMapping("/owner")
    private List<BookingResponseDto> findAllBookingForAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                                               @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {

        return bookingService.findAllBookingForAllItems(userId, state);
    }
}
