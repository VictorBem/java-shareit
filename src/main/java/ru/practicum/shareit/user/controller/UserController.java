package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Запрос всех пользователей
    @GetMapping
    private List<UserDto> getAll() {
        log.info("Get request to receive all users");
        return userService.getAll();
    }

    //создание пользователя
    @PostMapping
    private UserDto addUser(@Valid @RequestBody UserDto user) {
        log.info("Post new user with name {}", user.getName());
        return userService.addUser(user);
    }

    //Обновление пользователя
    @PatchMapping("/{id}")
    private UserDto updateUser(@PathVariable("id") long id, @RequestBody UserDto user) {
        log.info("Update user with name {}", user.getName());
        return userService.updateUser(id, user);
    }

    //Получение пользователя по id
    @GetMapping("/{id}")
    private UserDto getById(@PathVariable("id") long id) {
        log.info("Get user by id: {}", id);
        return userService.getById(id);
    }

    //Метод удаляет пользователя по его id
    @DeleteMapping("/{id}")
    private void deleteById(@PathVariable("id") long id) {
        log.info("Delete user by id: {}", id);
        userService.deleteById(id);
    }

}
