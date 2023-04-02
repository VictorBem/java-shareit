package ru.practicum.shareit.user.repository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;


@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    private long currentUserNumber = 0;

    //Метод создания нового пользователя
    @Override
    public User addUser(User user) {
        //Если уже существует пользователь с таким e-mail, то бросаем исключение
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.info("User with email {} is already exist.", user.getEmail());
            throw new IllegalArgumentException("User with email " + user.getEmail() + " is already exist.");
        }

        user.setId(getNextUserNumber());
        users.put(user.getId(), user);
        log.info("User with id {} was added.", users.get(user.getId()));

        return users.get(currentUserNumber);
    }

    //Метод обновления пользователя
    @Override
    public User updateUser(long id, User user) {
        if(!users.containsKey(id)) {
            log.info("User with id {} is not exist.", user.getId());
            throw new NoSuchElementException("User with id " + user.getId() + " is not exist.");
        }

        //Обновлять у пользователя существующий Email на Email, который присвоен другому пользователю нельзя
        if(users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()) && u.getId() != id)) {
            log.info("New email {} is already assigned to another user.", user.getEmail());
            throw new IllegalArgumentException("New email " + user.getEmail() + " is already assigned to another user.");
        }

        //В полях не переданных для обновления сохраняем прежние значения
        user.setId(id);
        if(user.getName() == null) {
            user.setName(users.get(id).getName());
        }
        if(user.getEmail() == null) {
            user.setEmail(users.get(id).getEmail());
        }

        users.replace(user.getId(), user);
        return users.get(user.getId());
    }

    //Метод получения пользователя по id
    @Override
    public User getById(long id) {
        return users.get(id);
    }

    //Метод удаления пользователя по id
    @Override
    public void deleteById(long id) {
        users.remove(id);
    }

    //Метод получения всех пользователей
    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    //Служебный метод нумерации пользователей
    private long getNextUserNumber() {
        return ++currentUserNumber;
    }

}
