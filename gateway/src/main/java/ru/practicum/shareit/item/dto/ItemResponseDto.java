package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.LastNextBooking;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private long requestId;
    private LastNextBooking lastBooking;
    private LastNextBooking nextBooking;
    private List<CommentResponseDto> comments;

}
