package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Integer duration;
    Set<Long> likes = new HashSet<>();
}