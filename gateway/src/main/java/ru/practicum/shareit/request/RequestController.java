package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    //создание запроса
    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                             @RequestBody RequestDto requestDto) {
        log.info("Post request to add new booking");
        return requestClient.addRequest(userId, requestDto);
    }

    //Метод возвращающий список всех запросов
    @GetMapping()
    public ResponseEntity<Object> findAllRequests(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId) {
        log.info("Get list of all requests");
        return requestClient.findAllRequests(userId);
    }

    //Метод возвращающий информацию по запросу по его id
    @GetMapping("{requestId}")
    public ResponseEntity<Object> findRequestById(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                                  @PathVariable(value = "requestId", required = true) long requestId) {
        log.info("Get request by id");
        return requestClient.findById(userId, requestId);
    }

    //Метод возвращающий одну из страниц с информацией по запросам
    @GetMapping("/all")
    public ResponseEntity<Object> findRequestById(@RequestHeader(value = "X-Sharer-User-Id", required = true) long userId,
                                                  @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", required = false, defaultValue = "99") Integer size) {
        log.info("Get list of all requests page by page");
        return requestClient.findAllRequestsByPages(userId, from, size);
    }

}
