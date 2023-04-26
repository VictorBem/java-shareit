package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utility.PageableUtility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    //Метод создания нового бронирования
    public BookingResponseDto addBooking(long userId, BookingDto bookingDto) {
        //Если пользователя не существует, то выбрасываем исключение
        if (!userRepository.existsById(userId)) {
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        }
        //Если вещи не существует или она не доступна, то выбрасываем исключение
        if (!itemRepository.existsById(bookingDto.getItemId())) {
            log.info("Item with id: {} is not exist.", bookingDto.getItemId());
            throw new NoSuchElementException("Item with id: " + bookingDto.getItemId() + " is not exist.");
        } else if (!itemRepository.findById(bookingDto.getItemId()).orElseThrow().isAvailable()) {
            log.info("Item: {} is not available.", bookingDto.getItemId());
            throw new BadRequestException("Item: " + bookingDto.getItemId() + " is not available.");
        }

        if (bookingDto.getStart() == null) {
            //Дата начала бронирования должна быть определена
            log.info("Start should be defined.");
            throw new BadRequestException("Start should be defined.");
        } else if (bookingDto.getEnd() == null) {
            //Дата окончания бронирования должна быть определена
            log.info("End date should be defined.");
            throw new BadRequestException("End date should be defined.");
        } else if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            //Если дата начала бронирования после даты окончания, то выбрасываем исключение
            log.info("Start: {} is after end: {} .", bookingDto.getStart(), bookingDto.getEnd());
            throw new BadRequestException("Start: " + bookingDto.getStart() + " is after end: " + bookingDto.getEnd() + " .");
        } else if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            //Если начало бронирования и окончание бронирования совпадают, то выбрасываем исключение
            log.info("Start: {} should be different from end: {}.", bookingDto.getStart(), bookingDto.getEnd());
            throw new BadRequestException("Start: " + bookingDto.getStart() + " should be different from end: " + bookingDto.getEnd() + " .");
        } else if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            //Дата начала бронирования должна быть в будущем
            log.info("Start date should be in the future.");
            throw new BadRequestException("Start date should be in the future.");
        }

        bookingDto.setItem(itemRepository.findById(bookingDto.getItemId()).orElseThrow());

        //Владелец вещи не может сам ее взять в аренду
        if (bookingDto.getItem().getOwner().getId() == userId) {
            log.info("Owner couldn't create booking.");
            throw new NoSuchElementException("Owner couldn't create booking.");
        }

        bookingDto.setBooker(userRepository.findById(userId).orElseThrow());
        bookingDto.setStatus(StatusOfBooking.WAITING);
        Booking savedBooking = bookingRepository.save(BookingMapper.toBooking(bookingDto));
        return BookingResponseMapper.toBookingResponseDto(savedBooking);
    }

    public BookingResponseDto updateBooking(long bookingId, long userId, boolean approved) {
        //Проверяем, что бронирование и пользователь существуют, а так же то, что пользователь является владельцем вещи
        checkUserAndBooking(userId, bookingId);
        Booking currentBooking = bookingRepository.findById(bookingId).orElseThrow();
        if (currentBooking.getItem().getOwner().getId() != userId) {
            log.info("Booking with id: {} has other owner.", userId);
            throw new NoSuchElementException("Booking with id: " + userId + " has other owner.");
        } else if (!currentBooking.getStatus().equals(StatusOfBooking.WAITING)) {
            log.info("Only booking in status Wait can be approved");
            throw new BadRequestException("Only booking in status Wait can be approved");
        }

        //Меняем статус бронирования
        if (approved) {
            currentBooking.setStatus(StatusOfBooking.APPROVED);
        } else {
            currentBooking.setStatus(StatusOfBooking.REJECTED);
        }

        //Обновляем бронирование
        currentBooking = bookingRepository.save(currentBooking);
        return BookingResponseMapper.toBookingResponseDto(currentBooking);
    }

    //Метод возвращающий бронирование (только для владельца вещи или того, кто создал бронирование)
    public BookingResponseDto findBookingByUserId(long bookingId, long userId) {
        //Проверяем корректность указанного в запросе пользователя и бронирования
        checkUserAndBooking(userId, bookingId);
        //Проверяем, что запрос делает владелец вещи или создатель бронирования и в этом случае возвращаем бронирование
        if (bookingRepository.findById(bookingId).orElseThrow().getItem().getOwner().getId() == userId
                || bookingRepository.findById(bookingId).orElseThrow().getBooker().getId() == userId) {
            return BookingResponseMapper.toBookingResponseDto(bookingRepository.findById(bookingId).orElseThrow());
        } else {
            log.info("Only owner of item or booker can review booking");
            throw new NoSuchElementException("Only owner of item or booker can review booking");
        }
    }

    public List<BookingResponseDto> findAllBookingByUserId(long userId, String state, Integer from, Integer size) {
        //Проверка на существование пользователя
        PageRequest currentPageRequest = null;
        if (!userRepository.existsById(userId)) {
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        } else if (from == null || size == null) {
            currentPageRequest = PageRequest.of(0, (int) bookingRepository.count(), Sort.by("start").descending());
        } else if (from < PageableUtility.MINIMUM_INDEX_OF_START_POSITION) {
            //Можно сформировать список бронирований только начиная с 0, отрицательные значения не допустимы
            log.info("Start position couldn't be negative, it's: {}", from);
            throw new BadRequestException("Start position couldn't be negative, it's: " + from);
        } else if (size < PageableUtility.MINIMUM_SIZE_OF_PAGE) {
            //Страница с бронированиями может минимально содержать один запрос, нулевые или отрицательные значения не допустимы
            log.info("Page should include at least one booking, now it's: {}", size);
            throw new BadRequestException("Page should include at least one booking, now it's: " + size);
        }


        if (currentPageRequest == null) {
            int currentPage = (from / size);
            currentPageRequest = PageRequest.of(currentPage, size, Sort.by("start").descending());
        }

        BookingState stateParam = BookingState.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        //Возвращаем бронирования запрошенного статуса
        switch (stateParam) {
            case WAITING: {
                return bookingRepository.getAllByBookerIdAndStatusOrderByStartDesc(userId, StatusOfBooking.WAITING, currentPageRequest)
                        .stream()
                        .map(BookingResponseMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            }
            case REJECTED: {
                return bookingRepository.getAllByBookerIdAndStatusOrderByStartDesc(userId, StatusOfBooking.REJECTED, currentPageRequest)
                        .stream()
                        .map(BookingResponseMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            }
            case ALL: {
                return bookingRepository.getAllByBookerIdOrderByStartDesc(userId, currentPageRequest)
                        .stream()
                        .map(BookingResponseMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            }
            case CURRENT: {
                return bookingRepository.getCurrentBookings(userId, currentPageRequest)
                        .stream()
                        .map(BookingResponseMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            }
            case FUTURE: {
                return bookingRepository.getFutureBookings(userId, currentPageRequest)
                        .stream()
                        .map(BookingResponseMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            }
            case PAST: {
                return bookingRepository.getPastBookings(userId, currentPageRequest)
                        .stream()
                        .map(BookingResponseMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            }
            default: {
                log.info("Unknown state: UNSUPPORTED_STATUS");
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
    }

    public List<BookingResponseDto> findAllBookingForAllItems(long userId, String state, Integer from, Integer size) {
        //Проверка на существование пользователя
        PageRequest currentPageRequest = null;
        if (!userRepository.existsById(userId)) {
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        } else if (from == null || size == null) {
            currentPageRequest = PageRequest.of(0, (int) bookingRepository.count(), Sort.by("start").descending());
        }
        else if (from < PageableUtility.MINIMUM_INDEX_OF_START_POSITION) {
            //Можно сформировать список бронирований только начиная с 0, отрицательные значения не допустимы
            log.info("Start position couldn't be negative, it's: {}", from);
            throw new BadRequestException("Start position couldn't be negative, it's: " + from);
        } else if (size < PageableUtility.MINIMUM_SIZE_OF_PAGE) {
            //Страница с бронированиями может минимально содержать один запрос, нулевые или отрицательные значения не допустимы
            log.info("Page should include at least one booking, now it's: {}", size);
            throw new BadRequestException("Page should include at least one booking, now it's: {}" + size);
        }

        if (currentPageRequest == null) {
            int currentPage = (from / size);
            currentPageRequest = PageRequest.of(currentPage, size, Sort.by("start").descending());
        }

        BookingState stateParam = BookingState.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        //Возвращаем бронирования запрошенного статуса
        switch (stateParam) {
            case WAITING: {
                return bookingRepository.getAllByItemOwnerIdAndState(userId, StatusOfBooking.WAITING, currentPageRequest)
                        .stream()
                        .map(BookingResponseMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            }
            case REJECTED: {
                return bookingRepository.getAllByItemOwnerIdAndState(userId, StatusOfBooking.REJECTED, currentPageRequest)
                        .stream()
                        .map(BookingResponseMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            }
            case ALL: {
                return bookingRepository.getAllByItemOwnerId(userId, currentPageRequest)
                        .stream()
                        .map(BookingResponseMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            }
            case CURRENT: {
                return bookingRepository.getCurrentBookingsByItems(userId, currentPageRequest)
                        .stream()
                        .map(BookingResponseMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            }
            case FUTURE: {
                return bookingRepository.getFutureBookingsByItems(userId, currentPageRequest)
                        .stream()
                        .map(BookingResponseMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            }
            case PAST: {
                return bookingRepository.getPastBookingsByItems(userId, currentPageRequest)
                        .stream()
                        .map(BookingResponseMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            }
            default: {
                log.info("Unknown state: UNSUPPORTED_STATUS");
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
    }

    //Служебный метод для проверки корректности пользователя и бронирования
    private void checkUserAndBooking(long userId, long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            log.info("Booking with id: {} is not exist.", bookingId);
            throw new NoSuchElementException("Booking with id: " + bookingId + " is not exist.");
        } else if (!userRepository.existsById(userId)) {
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        }
    }

}
