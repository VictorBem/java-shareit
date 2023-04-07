package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingResponseMapper {
    //Метод из объекта модели создает DTO-объект
    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    //Метод из DTO-объекта создает объекта модели
    public static Booking toBooking(BookingResponseDto bookingResponseDto) {
        Booking booking = new Booking();
        booking.setId(bookingResponseDto.getId());
        booking.setStart(bookingResponseDto.getStart());
        booking.setEnd(bookingResponseDto.getEnd());
        booking.setItem(bookingResponseDto.getItem());
        booking.setBooker(bookingResponseDto.getBooker());
        booking.setStatus(bookingResponseDto.getStatus());
        return booking;
    }
}
