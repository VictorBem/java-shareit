package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
