package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="name")
    private String name;
    @NotBlank(message = "Адрес электронной почты не должен быть пустым")
    @Email(message = "Указан некорректный адрес электронной почты.")
    @Column(name="email", nullable = false, unique = true)
    private String email;

}
