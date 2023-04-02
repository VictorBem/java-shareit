package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    //Метод добавления пользователя в БД
    User addUser(User user);

    //Метод обновления пользователя в БД
    User updateUser(long id, User user);

    //Метод получения пользователя по его id
    User getById(long id);

    //Метод удаления пользователя по его id
    void deleteById(long id);

    //Метод получения всех пользователей
    List<User> getAll();
}
