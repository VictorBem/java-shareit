package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.LastNextBooking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.dto.CommentResponseMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utility.PageableUtility;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private static final int ONLY_ONE_BOOKING = 1;
    private final static long NO_REQUEST_ID_FOR_ITEM_IN_ITEM_DTO = 0;

    //Метод добавления новой вещи в базу
    public ItemResponseDto addItem(long userId, ItemDto item) {
        //Если указан несуществующий пользователь или при создании новой вещи не указан статус ее доступности выбрасываем исключение
        if (item.getAvailable() == null) {
            log.info("No initial available");
            throw new BadRequestException("No initial available");
        } else if (item.getName().isBlank()) {
            log.info("Name field is mandatory and couldn't be empty");
            throw new BadRequestException("Name field is mandatory and couldn't be empty");
        } else if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.info("Description field is mandatory and couldn't be empty");
            throw new BadRequestException("Description field is mandatory and couldn't be empty");
        } else if (!userRepository.existsById(userId)) {
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        }
        //Определяем пользователя создающего вещь
        User currentUser = userRepository.findById(userId).orElseThrow();
        item.setOwner(currentUser);
        Item newItem = ItemMapper.toItem(item);
        //Определяем запрос по которому создана вещь
        if (item.getRequestId() != NO_REQUEST_ID_FOR_ITEM_IN_ITEM_DTO && requestRepository.existsById(item.getRequestId())) {
            newItem.setRequest(requestRepository.findById(item.getRequestId()).orElseThrow());
        }
        //Вызываем метод создания новой вещи в базе данных
        newItem = itemRepository.save(newItem);
        return ItemResponseMapper.toItemDto(newItem);
    }

    //Метод обновления вещи
    public ItemResponseDto updateItem(long userId, long itemId, ItemDto item) {
        //Item updatedItem;
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow();

        //Проверяем, что указанный пользователь и предмет существуют, а так же корректность владельца
        checkUserIdAndItemId(userId, itemId);

        item.setId(itemId);
        //В полях не переданных для обновления сохраняем прежние значения
        if (item.getName() == null) {
            item.setName(itemToUpdate.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(itemToUpdate.getDescription());
        }
        if (item.getOwner() == null) {
            item.setOwner(itemToUpdate.getOwner());
        }
        if (item.getAvailable() == null || item.getAvailable().isBlank()) {
            item.setAvailable(itemToUpdate.isAvailable() ? "true" : "false");
        }
        //Заполняем поле запроса на создание вещи, если вещь создавалась по запросу
        itemToUpdate = ItemMapper.toItem(item);
        if (item.getRequestId() != 0 && requestRepository.existsById(item.getRequestId())) {
            itemToUpdate.setRequest(requestRepository.findById(item.getRequestId()).orElseThrow());
        }

        itemToUpdate = itemRepository.save(itemToUpdate);
        return ItemResponseMapper.toItemDto(itemToUpdate);
    }

    //Получение вещи по id
    public ItemResponseDto getById(long userId, long itemId) {
        //Проверяем, что указанный предмет существуют
        if (!itemRepository.existsById(itemId)) {
            log.info("Item with id: {} is not exist.", itemId);
            throw new NoSuchElementException("Item with id: " + itemId + " is not exist.");
        }
        //Определяем текущую вещь
        ItemResponseDto currentItem = ItemResponseMapper.toItemDto(itemRepository.findById(itemId).orElseThrow());

        //Добавляем даты следующего и предыдущего бронирования, но только в том случае, если бронирования есть
        if (bookingRepository.getNextBooking(itemId, userId) != null && bookingRepository.getNextBooking(itemId, userId).size() > 0) {
            currentItem.setNextBooking(new LastNextBooking(bookingRepository.getNextBooking(itemId, userId).get(0).getId(),
                    bookingRepository.getNextBooking(itemId, userId).get(0).getBooker().getId()));
        } else {
            currentItem.setNextBooking(null);
        }

        if (bookingRepository.getLastBooking(itemId, userId) != null && bookingRepository.getLastBooking(itemId, userId).size() > 0) {
            currentItem.setLastBooking(new LastNextBooking(bookingRepository.getLastBooking(itemId, userId).get(0).getId(),
                    bookingRepository.getLastBooking(itemId, userId).get(0).getBooker().getId()));
        } else if (bookingRepository.countBookingsByItemIdAndItemOwnerId(itemId, userId) == ONLY_ONE_BOOKING) {
            //Если было только одно бронирование, то считаем его последним
            if (bookingRepository.getBookingByItemIdAndItemOwnerId(itemId, userId).getStatus() == StatusOfBooking.APPROVED) {
                currentItem.setLastBooking(new LastNextBooking(bookingRepository.getBookingByItemIdAndItemOwnerId(itemId, userId).getId(),
                        bookingRepository.getBookingByItemIdAndItemOwnerId(itemId, userId).getBooker().getId()));
            }
        } else {
            currentItem.setLastBooking(null);
        }

        //Добавляем комментарии
        if (commentRepository.getAllByItemId(itemId).size() > 0) {
            currentItem.setComments(commentRepository.getAllByItemId(itemId).stream()
                    .map(CommentResponseMapper::toItemDto)
                    .collect(Collectors.toList()));
        }

        return currentItem;
    }

    //Получение списка вещей по id владельца
    public List<ItemResponseDto> getItemsOfOwner(long userId, Integer from, Integer size) {
        //Список для обогащенных ручным методом вещей
        PageRequest currentPageRequest = null;
        List<ItemResponseDto> finalItems = new ArrayList<>();
        //Проверяем, что указанный пользователь существуют
        if (!userRepository.existsById(userId)) {
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        } else if (from == null || size == null) {
            currentPageRequest = PageRequest.of(0, (int) itemRepository.count(), Sort.by("id").descending());
        } else if (from < PageableUtility.MINIMUM_INDEX_OF_START_POSITION) {
            //Можно сформировать список бронирований только начиная с 0, отрицательные значения не допустимы
            log.info("Start position couldn't be negative, it's: {}", from);
            throw new BadRequestException("Start position couldn't be negative, it's: " + from);
        } else if (size < PageableUtility.MINIMUM_SIZE_OF_PAGE) {
            //Страница с бронированиями может минимально содержать один запрос, нулевые или отрицательные значения не допустимы
            log.info("Page should include at least one booking, now it's: {}", size);
            throw new BadRequestException("Page should include at least one booking, now it's: " + size);
        }

        //Получаем список всех вещей владельца
        if (currentPageRequest == null) {
            int currentPage = (from / size);
            currentPageRequest = PageRequest.of(currentPage, size, Sort.by("id").descending());
        }
        List<ItemResponseDto> resultItems = itemRepository.findItemByOwnerIdOrderByIdAsc(userId, currentPageRequest).stream()
                .map(ItemResponseMapper::toItemDto)
                .collect(Collectors.toList());

        //Получаем информацию обо всех бронированиях вещей владельца
        List<Booking> bookingsOfOwner = bookingRepository.getBookingsByItemsId(resultItems.stream()
                                                                                          .map(ItemResponseDto::getId)
                                                                                          .collect(Collectors.toList()));

        for (ItemResponseDto currentItem : resultItems) {
            //Определяем следующее бронирование для вещи
            long currentItemId = currentItem.getId();
            if (bookingsOfOwner.stream().filter(i -> i.getItem().getId() == currentItemId && i.getStatus() == StatusOfBooking.APPROVED).count() > 0) {
                Booking nextBooking = bookingsOfOwner.stream()
                        .filter(i -> i.getItem().getId() == currentItemId)
                        .filter(i -> i.getStatus() == StatusOfBooking.APPROVED)
                        .filter(i -> i.getStart().isAfter(LocalDateTime.now())).min((o1, o2) -> o1.getStart().isBefore(o2.getStart()) ? -1 : o1.getStart().isEqual(o2.getStart()) ? 0 : 1).orElseThrow();
                currentItem.setNextBooking(new LastNextBooking(nextBooking.getId(), nextBooking.getBooker().getId()));
            } else {
                currentItem.setNextBooking(null);
            }

            //Определяем последнее бронирование вещи
            if (bookingsOfOwner.stream().filter(i -> i.getItem().getId() == currentItemId && i.getStatus() == StatusOfBooking.APPROVED).count() > 0) {
                Booking lastBooking = bookingsOfOwner.stream()
                        .filter(i -> i.getItem().getId() == currentItemId)
                        .filter(i -> i.getStatus() == StatusOfBooking.APPROVED)
                        .filter(i -> i.getStart().isBefore(LocalDateTime.now())).max((o1, o2) -> o1.getStart().isBefore(o2.getStart()) ? 1 : o1.getStart().isEqual(o2.getStart()) ? 0 : -1).orElseThrow();
                currentItem.setLastBooking(new LastNextBooking(lastBooking.getId(), lastBooking.getBooker().getId()));
            } else {
                currentItem.setLastBooking(null);
            }
            finalItems.add(currentItem);
        }

        return finalItems;
    }

    //Получение списка доступных вещей по тексту входящему в название или описание
    public List<ItemResponseDto> getItemsWithText(String searchText, Integer from, Integer size) {
        PageRequest currentPageRequest = null;
        if (searchText.isBlank()) {
            return new ArrayList<>();
        } else if (from == null || size == null) {
            currentPageRequest = PageRequest.of(0, (int) itemRepository.count(), Sort.by("id").ascending());
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
            currentPageRequest = PageRequest.of(currentPage, size, Sort.by("id").ascending());
        }

        return itemRepository.searchAvailableItemsByText(searchText, currentPageRequest).stream()
                .map(ItemResponseMapper::toItemDto)
                .collect(Collectors.toList());
    }

    //Метод добавления комментария
    public CommentResponseDto addComment(long userId, long itemId, CommentDto commentDto) {
        //Проверяем, что пользователь брал вещь в аренду и аренд закончена
        if (bookingRepository.getAllByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now()).size() == 0) {
            log.info("User should have booking for item and booking should be finished");
            throw new BadRequestException("User should have booking for item and booking should be finished");
        } else if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            log.info("Comment couldn't be empty");
            throw new BadRequestException("Comment couldn't be empty");
        }

        Comment currentComment = new Comment();
        currentComment.setItem(itemRepository.findById(itemId).orElseThrow());
        currentComment.setAuthor(userRepository.findById(userId).orElseThrow());
        currentComment.setText(commentDto.getText());
        currentComment.setCreated(LocalDateTime.now());
        currentComment = commentRepository.save(currentComment);
        return CommentResponseMapper.toItemDto(currentComment);
    }

    //Служебный метод проверки вещи и пользователя
    private boolean checkUserIdAndItemId(long userId, long itemId) {
        if (!userRepository.existsById(userId)) {
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        } else if (!itemRepository.existsById(itemId)) {
            log.info("Item with id: {} is not exist.", itemId);
            throw new NoSuchElementException("Item with id: " + itemId + " is not exist.");
        } else if (itemRepository.findById(itemId).orElseThrow().getOwner().getId() != userId) {//itemRepository.findAll().stream().noneMatch(i -> i.getId() == itemId && i.getOwner().getId() == userId)) {
            log.info("Item with id: {} has owner with another id.", itemId);
            throw new NoSuchElementException("Item with id: " + itemId + " has owner with another id.");
        }
        return true;
    }

}
