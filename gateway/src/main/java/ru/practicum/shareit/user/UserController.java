package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    //создание пользователя
    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDto userDto) {
        log.info("Creating booking {}", userDto);
        return userClient.addUser(userDto);
    }

    //Обновление пользователя
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") long id, @RequestBody UserDto user) {
        log.info("Update user with name {}", user.getName());
        return userClient.updateUser(id, user);
    }

    //Получение пользователя по id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") long id) {
        log.info("Get user by id: {}", id);
        return userClient.getUser(id);
    }

    //Запрос всех пользователей
    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Get request to receive all users");
        return userClient.getAll();
    }

    //Метод удаляет пользователя по его id
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable("id") long id) {
        log.info("Delete user by id: {}", id);
        return userClient.deleteById(id);
    }
}
