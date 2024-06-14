package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class User {
    Long id;
    String email;
    String login;
    String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date birthday;
    Set<Long> filmIdLiked;
    List<Long> friends;
}