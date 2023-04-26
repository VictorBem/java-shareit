package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    //Поиск пользователя по совпадающему имени и email
    List<User> getUsersByNameAndEmail(String name, String email);

    //Поиск пользователя по email
    List<User> getUsersByEmail(String email);
}
