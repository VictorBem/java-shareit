package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ShortItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDtoMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utility.PageableUtility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    //Метод добавления нового запроса
    public RequestDto addRequest(long userId, RequestDto requestDto) {
        if (!userRepository.existsById(userId)) {
            //Если пользователя не существует, то выбрасываем исключение
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        } else if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            //Если отсутствует описание требуемой вещи, то выбрасываем исключение
            log.info("Description of item is mandatory ");
            throw new BadRequestException("Description of item is mandatory ");
        }

        requestDto.setCreated(LocalDateTime.now());
        requestDto.setRequestor(userRepository.findById(userId).orElseThrow());
        Request savedRequest = requestRepository.save(RequestMapper.toItemRequest(requestDto));
        return RequestMapper.toItemRequestDto(savedRequest);
    }

    //Метод возвращающий все запросы
    public List<ResponseRequestDto> findAllRequests(long userId) {
        if (!userRepository.existsById(userId)) {
            //Если пользователя не существует, то выбрасываем исключение
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        }

        //Получаем из БД все существующие запросы
        List<Request> requests = requestRepository.getAllByOrderByCreatedDesc();
        List<ResponseRequestDto> responseRequestDto = requests.stream()
                                                              .map(ResponseRequestDtoMapper::toResponseRequestDto)
                                                              .collect(Collectors.toList());

        //Обогащаем данные о запросах списком вещей созданных по этому запросу
        return addDetails(requests, responseRequestDto);

    }

    ///Метод возвращающий все запросы с разбивкой по страницам
    public List<ResponseRequestDto> findAllRequestsByPages(long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            //Если пользователя не существует, то выбрасываем исключение
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        } else if (from == null || size == null) {
            //Если не указаны номер позиции с которой выводятся запросы и количество запросов для вывода, то вызываем метод вывода всех запросов
            return findAllRequests(userId);
        } else if (from < PageableUtility.MINIMUM_INDEX_OF_START_POSITION) {
            //Можно сформировать список запросов только начиная с 0, отрицательные значения не допустимы
            log.info("Start position couldn't be negative, it's: {}", from);
            throw new BadRequestException("Start position couldn't be negative, it's: " + from);
        } else if (size < PageableUtility.MINIMUM_SIZE_OF_PAGE) {
            //Страница с результатами запроса может минимально содержать один запрос, нулевые или отрицательные значения не допустимы
            log.info("Page should include at least one request, now it's: {}", size);
            throw new BadRequestException("Page should include at least one request, now it's: {}" + size);
        }

        //Получаем из БД все существующие запросы
        List<Request> requests = requestRepository.getAllByOrderByCreatedDesc(PageRequest.of((from/size), size, Sort.by("created").descending()));
        List<ResponseRequestDto> responseRequestDto = requests.stream()
                                                              .filter(r -> r.getRequestor().getId() != userId)
                                                              .map(ResponseRequestDtoMapper::toResponseRequestDto)
                                                              .collect(Collectors.toList());

        return addDetails(requests, responseRequestDto);
    }

    //Метод возвращающий информацию по запросу по его id
    public ResponseRequestDto findById(long userId, long requestId) {
        if (!requestRepository.existsById(requestId)) {
            //Если пользователя не существует, то выбрасываем исключение
            log.info("Request with id: {} is not exist.", requestId);
            throw new NoSuchElementException("Request with id: " + requestId + " is not exist.");
        } else if (!userRepository.existsById(userId)) {
            //Если пользователя не существует, то выбрасываем исключение
            log.info("User with id: {} is not exist.", userId);
            throw new NoSuchElementException("User with id: " + userId + " is not exist.");
        }

        //Получаем из БД запрос по его id
        ResponseRequestDto responseRequestDto = ResponseRequestDtoMapper.toResponseRequestDto(requestRepository.findById(requestId).orElseThrow());

        //Обогащаем запрос списком вещей созданных по этому запросу
        List<Item> items = itemRepository.findAllByRequestId(requestId);

        responseRequestDto.setItems(items.stream()
                                         .filter(i -> i.getRequest().getId() == requestId)
                                         .map(ShortItemDtoMapper::toShortItemDto)
                                         .collect(Collectors.toList()));

        return responseRequestDto;
    }

    //Метод добавляет информацию о вещах созданных по запросу.
    private List<ResponseRequestDto> addDetails(List<Request> requests, List<ResponseRequestDto> responseRequestDto) {
        List<Item> items = itemRepository.getAllByRequestsId(requests.stream()
                .map(Request::getId)
                .collect(Collectors.toList()));

        return responseRequestDto.stream()
                .peek(e -> e.setItems(items.stream()
                        .filter(i -> i.getRequest().getId() == e.getId())
                        .map(ShortItemDtoMapper::toShortItemDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
