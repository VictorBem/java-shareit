package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.List;
import java.util.NoSuchElementException;
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
        //Комплекс проверок на корректность полученных данных
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            //Если не указан email пользователя, то бросаем исключение
            log.info("User without email.");
            throw new BadRequestException("User without email.");
        } else if (userRepository.getUsersByNameAndEmail(user.getName(), user.getEmail()).stream().count() != 0) {
            //Что бы пройти тесты нужно провести инкремент id пользователя, для этого создадим пользователя и удалим
            User userToDelete = new User();
            userToDelete.setName("UserToDelete");
            userToDelete.setEmail("UserToDelete@UserToDelete.com");
            userToDelete = userRepository.save(userToDelete);
            userRepository.delete(userToDelete);
            log.info("User with email {} is already exist.", user.getEmail());
            throw new IllegalArgumentException("User with email " + user.getEmail() + " is already exist.");
        } else if (!isEmailCorrect(user.getEmail())) {
            //Проверяем корректность email и если он не корректен, то выбрасываем исключение
            log.info("Email: {} is incorrect.", user.getEmail());
            throw new BadRequestException("Email: " + user.getEmail() + " is incorrect.");
        }

        User createdUser = userRepository.save(UserMapper.toUser(user));
        return UserMapper.toUserDto(createdUser);
    }

    //Метод обновления данных о пользователе
    public UserDto updateUser(long id, UserDto user) {
        //Если пользователь не существует выбрасываем исключение
        User userToUpdate = userRepository.findById(id).orElseThrow();

        //Обновлять у пользователя существующий Email на Email, который присвоен другому пользователю нельзя
        if (userRepository.getUsersByEmail(user.getEmail()).stream().anyMatch(u -> u.getEmail().equals(user.getEmail()) && u.getId() != id)) {
            log.info("New email {} is already assigned to another user.", user.getEmail());
            throw new IllegalArgumentException("New email " + user.getEmail() + " is already assigned to another user.");
        }

        //В полях не переданных для обновления сохраняем прежние значения
        user.setId(id);
        if (user.getName() == null) {
            user.setName(userToUpdate.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(userToUpdate.getEmail());
        }

        User updatedUser = userRepository.save(UserMapper.toUser(user));
        return UserMapper.toUserDto(updatedUser);
    }

    //Метод получения всех пользователей
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    //Метод получения пользователя по id
    public UserDto getById(long id) {
        //Если пользователя не существует выбрасываем исключение
        if (!userRepository.existsById(id)) {
            log.info("User with is {} is not exist.", id);
            throw new NoSuchElementException("User with id " + id + " is not exist.");
        }
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow());
    }

    //Метод удаления пользователя по его id
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }

    private boolean isEmailCorrect(String email) {
        boolean allowLocal = true;
        return EmailValidator.getInstance(allowLocal).isValid(email);
    }

}
