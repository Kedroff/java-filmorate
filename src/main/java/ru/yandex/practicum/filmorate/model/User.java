package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class User {
    Long id;
    String email;
    String login;
    String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;
    Set<Long> friends = new HashSet<>();
}