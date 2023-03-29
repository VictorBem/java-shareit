package ru.practicum.shareit.user.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    //Запрос всех пользователей
    @GetMapping
    private List<UserDto> getAll() {
        return service.getAll();
    }

    //создание пользователя
    @PostMapping
    private UserDto addUser(@Valid @RequestBody UserDto user) {
        return service.addUser(user);
    }

    //Обновление пользователя
    @PatchMapping("/{id}")
    private UserDto updateUser(@PathVariable("id") long id, @RequestBody UserDto user) {
        return service.updateUser(id, user);
    }

    //Получение пользователя по id
    @GetMapping("/{id}")
    private UserDto getById(@PathVariable("id") int id) {
        return service.getById(id);
    }


    //Метод удаляет пользователя по его id
    @DeleteMapping("/{id}")
    private void deleteById(@PathVariable("id") int id) {
        service.deleteById(id);
    }

}
