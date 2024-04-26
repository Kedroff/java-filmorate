package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Integer duration;
}