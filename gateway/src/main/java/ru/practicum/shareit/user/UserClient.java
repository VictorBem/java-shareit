package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    //Создание пользователя
    public ResponseEntity<Object> addUser(UserDto requestDto) {
        return post("", requestDto);
    }

    //Обновление пользователя
    public ResponseEntity<Object> updateUser(long id, UserDto requestDto) {
        return patch("/" + id, id,  requestDto);
    }

    //Получение информации о пользователе
    public ResponseEntity<Object> getUser(long userId) {
        return get("/" + userId);
    }

    //Получение информации о всех пользователях
    public ResponseEntity<Object> getAll() {
        return get("");
    }

    //Удаление пользователя по Id
    public ResponseEntity<Object> deleteById(long userId) {
        return delete("/" + userId);
    }


}
