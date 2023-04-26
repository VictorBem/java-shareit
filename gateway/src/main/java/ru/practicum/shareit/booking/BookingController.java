package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
											  @RequestParam(name = "state", defaultValue = "ALL") String state,
			                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
		                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	//Обновление бронирования
	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> update(@PathVariable("bookingId") long bookingId,
										 @RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
										 @RequestParam("approved") boolean approved) {
		log.info("Patch request for booking with id: {} .", bookingId);
		return bookingClient.updateBooking(bookingId, userId, approved);
	}

	//Все бронирования вещей владельца определенного по id
	@GetMapping("/owner")
	public ResponseEntity<Object> findAllBookingForAllItems(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
															@RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
															@RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
															@RequestParam(value = "size", required = false, defaultValue = "99") Integer size) {
		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.findAllBookingForAllItems(userId, state, from, size);
	}
}
