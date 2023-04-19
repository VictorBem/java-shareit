package ru.practicum.shareit.comment.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.model.Comment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDtoMapper {
    //Метод из объекта модели создает DTO-объект
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getText()
        );
    }

    //Метод из DTO-объекта создает объекта модели
    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }
}
