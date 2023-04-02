package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    private long id;
    private String description;
    private User requestor;
    private LocalDateTime created;

}
