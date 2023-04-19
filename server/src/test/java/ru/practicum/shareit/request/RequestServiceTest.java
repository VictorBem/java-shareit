package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDtoMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {
    @Autowired
    private RequestService requestService;
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
    private Request testRequest;
    private Request testRequest1;
    private List<Request> requests;
    private Request request;
    private RequestDto requestDto;
    private ResponseRequestDto responseRequestDto;
    private ResponseRequestDto responseRequestDto1;
    private List<ResponseRequestDto> responseRequestDtoList;

    @Test
    void addRequestTest() {
        prepareDataForTest();

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testUser));

        Mockito
                .when(requestRepository.save(Mockito.any(Request.class)))
                .thenReturn(request);

        RequestDto response = requestService.addRequest(1, requestDto);

        assertEquals(response.getId(), requestDto.getId(), "Созданные вещи не совпадают");
        assertEquals(response.getDescription(), requestDto.getDescription(), "Созданные вещи не совпадают");
        assertEquals(response.getRequestor(), requestDto.getRequestor(), "Созданные вещи не совпадают");
    }

    @Test
    void findAllRequests() {
        prepareDataForTest();
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(requestRepository.getAllByOrderByCreatedDesc())
                .thenReturn(requests);

        Mockito
                .when(requestRepository.save(Mockito.any(Request.class)))
                .thenReturn(request);

        List<ResponseRequestDto> response = requestService.findAllRequests(1);

        assertEquals(response.get(0).getId(), requests.get(0).getId(), "Созданные вещи не совпадают");
        assertEquals(response.get(0).getDescription(), requests.get(0).getDescription(), "Созданные вещи не совпадают");
        assertEquals(response.get(0).getCreated(), requests.get(0).getCreated(), "Созданные вещи не совпадают");
        assertEquals(response.get(1).getId(), requests.get(1).getId(), "Созданные вещи не совпадают");
        assertEquals(response.get(1).getDescription(), requests.get(1).getDescription(), "Созданные вещи не совпадают");
        assertEquals(response.get(1).getCreated(), requests.get(1).getCreated(), "Созданные вещи не совпадают");
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

        requests = new ArrayList<>();
        requests.add(testRequest);
        requests.add(testRequest1);

        responseRequestDto = new ResponseRequestDto();
        responseRequestDto = ResponseRequestDtoMapper.toResponseRequestDto(testRequest);

        responseRequestDto1 = new ResponseRequestDto();
        responseRequestDto1 = ResponseRequestDtoMapper.toResponseRequestDto(testRequest1);

        responseRequestDtoList = new ArrayList<>();
        responseRequestDtoList.add(responseRequestDto);
        responseRequestDtoList.add(responseRequestDto1);

        request = new Request();
        request.setId(1);
        request.setDescription("Test request");
        request.setRequestor(testUser);
        request.setCreated(LocalDateTime.of(2023, 3, 1, 11, 23));

        requestDto = new RequestDto(1,"Test request",testUser,LocalDateTime.of(2023, 3, 1, 11, 23));
    }
}
