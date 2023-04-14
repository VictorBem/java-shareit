package ru.practicum.shareit.Item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.practicum.shareit.booking.model.Booking;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final ItemService itemService;
    @MockBean
    UserRepository userRepository;
    @MockBean
    RequestRepository requestRepository;
    @MockBean
    ItemRepository itemRepository;
    @MockBean
    BookingRepository bookingRepository;
    @MockBean
    CommentRepository commentRepository;
    private User testUser;
    private User testUser1;
    private Item testItem;
    private Item testItem1;
    private List<Item> itemsTest;
    private Request testRequest;
    private Request testRequest1;
    private ItemResponseDto itemResponseDto;
    private ItemResponseDto itemResponseDto1;
    private List<ItemResponseDto> items;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private CommentResponseDto commentResponseDto;
    private Comment comment;
    private Request request;
    private Booking booking;
    private Booking booking1;
    private List<Booking> bookings;

    //Добавление новой вещи, если все параметры указаны корректно
    @Test
    void addItemIfEverythingIsOk() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testUser1));

        Mockito
                .when(requestRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(testItem);

        ItemResponseDto newItem = itemService.addItem(2, itemDto);

        assertEquals(newItem.getId(), testItem.getId(), "Созданные вещи не совпадают");
        assertEquals(newItem.getDescription(), testItem.getDescription(), "Созданные вещи не совпадают");
        assertEquals(newItem.getName(), testItem.getName(), "Созданные вещи не совпадают");
        assertEquals(newItem.isAvailable(), testItem.isAvailable(), "Созданные вещи не совпадают");
        assertEquals(newItem.getRequestId(), testItem.getRequest().getId(), "Созданные вещи не совпадают");
    }

    //Добавление новой вещи, если все параметры указаны корректно
    @Test
    void addItemIfAvailableIsNull() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testUser1));

        Mockito
                .when(requestRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(testItem);

        //Создаем ошибку в данных
        itemDto.setAvailable(null);
        BadRequestException exp = Assertions.assertThrows(BadRequestException.class, () -> itemService.addItem(2, itemDto));
        assertEquals(exp.getMessage(), "No initial available", "Должно быть выброшено исключение BadRequestException");
    }

    //Добавление новой вещи, если пустое имя
    @Test
    void addItemIfNameIsBlank() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testUser1));

        Mockito
                .when(requestRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(testItem);

        //Создаем ошибку в данных
        itemDto.setName("");
        BadRequestException exp = Assertions.assertThrows(BadRequestException.class, () -> itemService.addItem(2, itemDto));
        assertEquals(exp.getMessage(), "Name field is mandatory and couldn't be empty", "Должно быть выброшено исключение BadRequestException");
    }

    //Добавление новой вещи, если отсутствует описание
    @Test
    void addItemIfDescriptionIsBlank() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testUser1));

        Mockito
                .when(requestRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(testItem);

        //Создаем ошибку в данных
        itemDto.setDescription("");
        BadRequestException exp = Assertions.assertThrows(BadRequestException.class, () -> itemService.addItem(2, itemDto));
        assertEquals(exp.getMessage(), "Description field is mandatory and couldn't be empty", "Должно быть выброшено исключение BadRequestException");
    }

    //Добавление новой вещи, если пользователь не существует
    @Test
    void addItemIfUserDoesNotExist() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Mockito
                .when(requestRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(testItem);

        //Создаем ошибку в данных
        NoSuchElementException exp = Assertions.assertThrows(NoSuchElementException.class, () -> itemService.addItem(2, itemDto));
        assertEquals(exp.getMessage(), "User with id: 2 is not exist.", "Должно быть выброшено исключение BadRequestException");
    }

    //Добавление новой вещи без запроса
    @Test
    void addItemIfNoRequest() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testUser1));

        Mockito
                .when(requestRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(testItem);

        ItemResponseDto newItem = itemService.addItem(2, itemDto);

        assertEquals(newItem.getId(), testItem.getId(), "Созданные вещи не совпадают");
        assertEquals(newItem.getDescription(), testItem.getDescription(), "Созданные вещи не совпадают");
        assertEquals(newItem.getName(), testItem.getName(), "Созданные вещи не совпадают");
        assertEquals(newItem.isAvailable(), testItem.isAvailable(), "Созданные вещи не совпадают");
    }

    //Изменение вещи
    @Test
    void updateItemItemIfEverythingIsOr() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testUser1));
        Mockito
                .when(requestRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(testItem);

        ItemResponseDto newItem = itemService.updateItem(2, 1, itemDto);

        assertEquals(newItem.getId(), testItem.getId(), "Созданные вещи не совпадают");
        assertEquals(newItem.getDescription(), testItem.getDescription(), "Созданные вещи не совпадают");
        assertEquals(newItem.getName(), testItem.getName(), "Созданные вещи не совпадают");
        assertEquals(newItem.isAvailable(), testItem.isAvailable(), "Созданные вещи не совпадают");
    }

    //Добавление новой вещи без запроса - без полей наименование, описание, владелец, доступность
    @Test
    void updateItemItemIfSomeFieldDontNeedToUpdate() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testUser1));
        Mockito
                .when(requestRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(testItem);

        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);
        itemDto.setOwner(null);
        ItemResponseDto newItem = itemService.updateItem(2, 1, itemDto);

        assertEquals(newItem.getId(), testItem.getId(), "Созданные вещи не совпадают");
        assertEquals(newItem.getDescription(), testItem.getDescription(), "Созданные вещи не совпадают");
        assertEquals(newItem.getName(), testItem.getName(), "Созданные вещи не совпадают");
        assertEquals(newItem.isAvailable(), testItem.isAvailable(), "Созданные вещи не совпадают");
    }

    //Обновление новой вещи - случай исключения возникает исключение
    @Test
    void addItemIfExceptionHappened() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testUser1));
        Mockito
                .when(requestRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(testItem);

        //Создаем ошибку в данных
        NoSuchElementException exp = Assertions.assertThrows(NoSuchElementException.class, () -> itemService.updateItem(2, 1, itemDto));
        assertEquals(exp.getMessage(), "User with id: 2 is not exist.", "Должно быть выброшено исключение BadRequestException");
    }

    //Обновление новой вещи - случай исключения возникает исключение
    @Test
    void addItemIfExceptionHappenedNoItem() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testUser1));
        Mockito
                .when(requestRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(testItem);

        //Создаем ошибку в данных
        NoSuchElementException exp = Assertions.assertThrows(NoSuchElementException.class, () -> itemService.updateItem(2, 1, itemDto));
        assertEquals(exp.getMessage(), "Item with id: 1 is not exist.", "Должно быть выброшено исключение BadRequestException");
    }

    //Обновление новой вещи - случай исключения возникает исключение
    @Test
    void addItemIfExceptionHappenedWrongOwner() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testUser1));
        Mockito
                .when(requestRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(testItem);
        //В вызове указан некорректный пользователь
        NoSuchElementException exp = Assertions.assertThrows(NoSuchElementException.class, () -> itemService.updateItem(3, 1, itemDto));
        assertEquals(exp.getMessage(), "Item with id: 1 has owner with another id.", "Должно быть выброшено исключение BadRequestException");
    }

    @Test
    void getItemsOfOwnerIfEverythingIsOk() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.count())
                .thenReturn(2L);
        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(itemRepository.findItemByOwnerIdOrderByIdAsc(Mockito.any(Long.class), Mockito.any(Pageable.class)))
                .thenReturn(itemsTest);

        List<ItemResponseDto> response = itemService.getItemsOfOwner(2, 1, 999);

        assertEquals(response.get(0).getId(), testItem.getId(), "Созданные вещи не совпадают");
        assertEquals(response.get(0).getDescription(), testItem.getDescription(), "Созданные вещи не совпадают");
        assertEquals(response.get(0).getName(), testItem.getName(), "Созданные вещи не совпадают");
        assertEquals(response.get(0).isAvailable(), testItem.isAvailable(), "Созданные вещи не совпадают");
        assertEquals(response.get(1).getId(), testItem1.getId(), "Созданные вещи не совпадают");
        assertEquals(response.get(1).getDescription(), testItem1.getDescription(), "Созданные вещи не совпадают");
        assertEquals(response.get(1).getName(), testItem1.getName(), "Созданные вещи не совпадают");
        assertEquals(response.get(1).isAvailable(), testItem1.isAvailable(), "Созданные вещи не совпадают");
    }

    @Test
    void getItemsWithTextIfEverythingIsOk() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.count())
                .thenReturn(2L);
        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(itemRepository.searchAvailableItemsByText(Mockito.any(String.class), Mockito.any(Pageable.class)))
                .thenReturn(itemsTest);

        List<ItemResponseDto> response = itemService.getItemsWithText("test", 0, 999);

        assertEquals(response.get(0).getId(), testItem.getId(), "Созданные вещи не совпадают");
        assertEquals(response.get(0).getDescription(), testItem.getDescription(), "Созданные вещи не совпадают");
        assertEquals(response.get(0).getName(), testItem.getName(), "Созданные вещи не совпадают");
        assertEquals(response.get(0).isAvailable(), testItem.isAvailable(), "Созданные вещи не совпадают");
        assertEquals(response.get(1).getId(), testItem1.getId(), "Созданные вещи не совпадают");
        assertEquals(response.get(1).getDescription(), testItem1.getDescription(), "Созданные вещи не совпадают");
        assertEquals(response.get(1).getName(), testItem1.getName(), "Созданные вещи не совпадают");
        assertEquals(response.get(1).isAvailable(), testItem1.isAvailable(), "Созданные вещи не совпадают");
    }

    @Test
    void addCommentIfEverythingIsOk() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(bookingRepository.getAllByBookerIdAndItemIdAndEndIsBefore(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(bookings);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testUser));
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(comment);

        CommentResponseDto response = itemService.addComment(1, 1, commentDto);

        assertEquals(response.getText(), commentDto.getText(), "Созданные вещи не совпадают");
    }

    @Test
    void getByIdIfEverythingIsOk() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testItem));
        Mockito
                .when(bookingRepository.getNextBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(null);
        Mockito
                .when(bookingRepository.countBookingsByItemIdAndItemOwnerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(999L);
        Mockito
                .when(commentRepository.getAllByItemId(Mockito.anyLong()))
                .thenReturn(new ArrayList<>());


        ItemResponseDto response = itemService.getById(1, 1);

        assertEquals(response.getId(), testItem.getId(), "Созданные вещи не совпадают");
        assertEquals(response.getName(), testItem.getName(), "Созданные вещи не совпадают");
        assertEquals(response.getDescription(), testItem.getDescription(), "Созданные вещи не совпадают");
        assertEquals(response.getOwner(), testItem.getOwner(), "Созданные вещи не совпадают");
        assertEquals(response.getRequestId(), testItem.getRequest().getId(), "Созданные вещи не совпадают");
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

        //Создаем запрос для теста
        testRequest = new Request();
        testRequest.setId(1);
        testRequest.setRequestor(testUser1);
        testRequest.setCreated(LocalDateTime.of(2023, 4, 11, 20, 23));
        testRequest.setDescription("I need test Item)))");

        testRequest1 = new Request();
        testRequest1.setId(2);
        testRequest1.setRequestor(testUser1);
        testRequest1.setCreated(LocalDateTime.of(2023, 4, 11, 20, 23));
        testRequest1.setDescription("I need test Item)))");

        //Создаем вещь для теста
        testItem = new Item();
        testItem.setId(1);
        testItem.setDescription("Test item");
        testItem.setName("Test item");
        testItem.setOwner(testUser1);
        testItem.setAvailable(true);
        testItem.setRequest(testRequest);

        testItem1 = new Item();
        testItem1.setId(2);
        testItem1.setDescription("Test item 1");
        testItem1.setName("Test item 1");
        testItem1.setOwner(testUser1);
        testItem1.setAvailable(true);
        testItem1.setRequest(testRequest1);

        itemsTest = new ArrayList<>();
        itemsTest.add(testItem);
        itemsTest.add(testItem1);

        //DTO для запроса
        itemDto = new ItemDto(1,
                "Test ItemDto",
                "Description ItemDto",
                "true",
                testUser1,
                testRequest.getId());

        itemResponseDto = new ItemResponseDto(1,
                "Test ItemDto",
                "Description ItemDto",
                true,
                testUser1,
                testRequest.getId(),
                null,
                null,
                null);

        itemResponseDto1 = new ItemResponseDto(1,
                "Test ItemDto1",
                "Description ItemDto1",
                false,
                testUser,
                testRequest.getId(),
                null,
                null,
                null);

        request = new Request();
        request.setId(1);
        request.setDescription("Test request");
        request.setRequestor(testUser);
        request.setCreated(LocalDateTime.of(2023, 3, 1, 11, 23));

        //Создаем DTO для комментария
        comment = new Comment();
        comment.setId(1);
        comment.setText("Test Comment 1");
        comment.setItem(testItem);
        comment.setCreated(LocalDateTime.of(2023, 4, 10, 22, 1));
        comment.setAuthor(testUser);

        commentDto = new CommentDto();
        commentDto.setText("Test Comment 1");

        commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(1);
        commentResponseDto.setText("Test Comment 1");
        commentResponseDto.setItem(testItem);
        commentResponseDto.setCreated(LocalDateTime.of(2023, 4, 10, 22, 1));
        commentResponseDto.setAuthorName("Test User");

        items = new LinkedList<>();
        items.add(itemResponseDto);
        items.add(itemResponseDto1);

        //Бронирования
        booking = new Booking();
        booking.setId(1);
        booking.setItem(testItem);
        booking.setStatus(StatusOfBooking.APPROVED);
        booking.setStart(LocalDateTime.of(1999,1,1,1,1));
        booking.setEnd(LocalDateTime.of(2000,1,1,1,1));
        booking.setBooker(testUser);

        booking1 = new Booking();
        booking1.setId(1);
        booking1.setItem(testItem);
        booking1.setStatus(StatusOfBooking.APPROVED);
        booking1.setStart(LocalDateTime.of(1999,1,1,1,1));
        booking1.setEnd(LocalDateTime.of(2000,1,1,1,1));
        booking1.setBooker(testUser);

        bookings = new ArrayList<>();
        bookings.add(booking);
        bookings.add(booking1);
    }
}
