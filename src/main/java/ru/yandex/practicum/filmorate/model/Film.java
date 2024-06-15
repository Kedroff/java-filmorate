package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Film {
    long id;
    @NotBlank
    String name;
    String description;
    LocalDate releaseDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Integer duration;
    List<Long> likes;
    Mpa mpa;
    List<GenreDao> genres;
}