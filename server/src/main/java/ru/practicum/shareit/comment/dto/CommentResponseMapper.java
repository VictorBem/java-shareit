package ru.practicum.shareit.comment.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.model.Comment;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponseMapper {
    //Метод из объекта модели создает DTO-объект
    public static CommentResponseDto toItemDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    //Метод из DTO-объекта создает объекта модели
    public static Comment toItem(CommentResponseDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setItem(commentDto.getItem());
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

}
