package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //Метод возвращает список бронирований указанного статуса для пользователя по его id
    List<Booking> getAllByBookerIdAndStatusOrderByStartDesc(long bookerId, StatusOfBooking state);

    //Метод возвращает все бронирования указанного пользователя по его id
    List<Booking> getAllByBookerIdOrderByStartDesc(long bookerId);

    //Метод возвращает текущие бронирования пользователя
    @Query(" select b from Booking b " +
           "where current_timestamp between b.start and b.end " +
           "  and b.booker.id = ?1 " +
           "order by b.start desc ")
    List<Booking> getCurrentBookings(long bookerId);

    //Метод возвращает будущие бронирования пользователя
    @Query(" select b from Booking b " +
           "where b.start > current_timestamp " +
           "  and b.booker.id = ?1 " +
           "order by b.start desc ")
    List<Booking> getFutureBookings(long bookerId);

    //Метод возвращает прошедшие бронирования пользователя
    @Query(" select b from Booking b " +
           "where b.end < current_timestamp " +
           "  and b.booker.id = ?1 " +
           "order by b.start desc ")
    List<Booking> getPastBookings(long bookerId);

    //Метод возвращает список всех бронирований по вещам указанного пользователя
    @Query(" select b from Booking b " +
           "where b.item.owner.id = ?1 " +
           "order by b.start desc ")
    List<Booking> getAllByItemOwnerId(long ownerId);

    //Метод возвращает список бронирований по вещам указанного пользователя с определенным статусом
    @Query(" select b from Booking b " +
           "where b.item.owner.id = ?1 " +
           "  and b.status = ?2 " +
           "order by b.start desc ")
    List<Booking> getAllByItemOwnerIdAndState(long ownerId, StatusOfBooking state);

    //Метод возвращает текущие бронирования по вещам пользователя
    @Query(" select b from Booking b " +
           "where current_timestamp between b.start and b.end " +
           "  and b.item.owner.id = ?1 " +
           "order by b.start desc ")
    List<Booking> getCurrentBookingsByItems(long ownerId);

    //Метод возвращает будущие бронирования по вещам пользователя
    @Query(" select b from Booking b " +
           "where b.start > current_timestamp " +
           "  and b.item.owner.id = ?1 " +
           "order by b.start desc ")
    List<Booking> getFutureBookingsByItems(long ownerId);

    //Метод возвращает прошедшие бронирования по вещам пользователя
    @Query("select b from Booking b " +
           "where b.end < current_timestamp " +
           "  and b.item.owner.id = ?1 " +
           "order by b.start desc ")
    List<Booking> getPastBookingsByItems(long ownerId);

    //Метод возвращающий следующее бронирование вещи
    @Query("select b from Booking b " +
           "where b.start > current_timestamp " +
           "  and b.item.id = ?1 " +
           "  and b.item.owner.id = ?2 " +
           "  and b.status = 'APPROVED' " +
           "order by b.start asc ")
    List<Booking> getNextBooking(long itemId, long userId);

    //Метод возвращающий последнее бронирование вещи
    @Query("select b from Booking b " +
            "where b.end < current_timestamp " +
            "  and b.item.id = ?1 " +
            "  and b.item.owner.id = ?2 " +
            "  and b.status = 'APPROVED' " +
            "order by b.end desc " )
    List<Booking> getLastBooking(long itemId, long userId);

    //Метод возвращает все бронирования по номеру вещи
    List<Booking> getAllByItemId(long itemId);

    //Метод возвращает завершенные бронирования арендатора
    List<Booking> getAllByBookerIdAndItemIdAndEndIsBefore(long bookerId, long itemId, LocalDateTime now);

    //Метод возвращает количество бронирований вещи
    long countBookingsByItemIdAndItemOwnerId(long itemId, long ownerId);

    Booking getBookingByItemIdAndItemOwnerId(long itemId, long ownerId);

    @Query("select b from Booking b " +
            "where b.item.id in ?1 " )
    List<Booking> getBookingsByItemsId(List<Long> items);

}
