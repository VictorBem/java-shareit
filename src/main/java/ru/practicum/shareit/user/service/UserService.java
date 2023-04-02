package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Метод создания нового пользователя
    public UserDto addUser(UserDto user) {
        User createdUser = userRepository.addUser(UserMapper.toUser(user));
        return UserMapper.toUserDto(createdUser);
    }

    //Метод обновления данных о пользователе
    public UserDto updateUser(long id, UserDto user) {
        User updatedUser = userRepository.updateUser(id, UserMapper.toUser(user));
        return UserMapper.toUserDto(updatedUser);
    }

    //Метод получения всех пользователей
    public List<UserDto> getAll() {
        return userRepository.getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    //Метод получения пользователя по id
    public UserDto getById(long id) {
        return  UserMapper.toUserDto(userRepository.getById(id));
    }

    //Метод удаления пользователя по его id
    public void deleteById(int id) {
        userRepository.deleteById(id);
    }

}
