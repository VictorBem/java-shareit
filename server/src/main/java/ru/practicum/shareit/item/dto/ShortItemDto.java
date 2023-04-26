package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long ownerId;
    private long requestId;
}
