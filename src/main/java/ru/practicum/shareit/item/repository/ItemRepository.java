package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    //Поиск вещи по ID владельца
    List<Item> findItemByOwnerIdOrderByIdAsc(long ownerId);

    //Поиск любой (доступной или не доступной) вещи по части наименования или описания
    @Query(" select i from Item i " +
           "where upper(i.name) like upper(concat('%', ?1, '%')) " +
           "   or upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> search(String text);

    //Поиск доступной вещи по части наименования или описания
    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "   or upper(i.description) like upper(concat('%', ?1, '%')) " +
            "  and i.available = true ")
    List<Item> searchAvailableItemsByText(String text);
}
