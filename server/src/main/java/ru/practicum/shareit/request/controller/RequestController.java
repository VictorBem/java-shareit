package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    //Метод создания запроса на вещь
    @PostMapping
    private RequestDto addRequest(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                  @RequestBody RequestDto requestDto) {
        log.info("Post request to add new booking");
        return requestService.addRequest(userId, requestDto);
    }

    //Метод возвращающий список всех запросов
    @GetMapping()
    private List<ResponseRequestDto> findAllRequests(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId) {
        log.info("Get list of all requests");
        return requestService.findAllRequests(userId);
    }

    //Метод возвращающий информацию по запросу по его id
    @GetMapping("{requestId}")
    private ResponseRequestDto findRequestById(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                               @PathVariable(value = "requestId", required = true) long requestId) {
        log.info("Get request by id");
        return requestService.findById(userId, requestId);
    }

    //Метод возвращающий одну из страниц с информацией по запросам
    @GetMapping("/all")
    private List<ResponseRequestDto> findRequestById(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                                     @RequestParam(value = "from", required = false) Integer from,
                                                     @RequestParam(value = "size", required = false) Integer size) {
        log.info("Get list of all requests page by page");
        return requestService.findAllRequestsByPages(userId, from, size);
    }
}
