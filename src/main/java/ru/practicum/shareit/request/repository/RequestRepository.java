package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    //Метод возвращает список запросов отсортированных по дате создания
    List<Request> getAllByOrderByCreatedDesc();

    List<Request> getAllByOrderByCreatedDesc(Pageable pageable);
}

