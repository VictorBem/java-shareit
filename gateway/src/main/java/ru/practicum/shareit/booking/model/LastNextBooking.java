package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LastNextBooking {

    //id бронирования
    private long id;

    //id пользователя создавшего бронирование
    private long bookerId;


}
