package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@RequestBody Film film) {
        try {
            validateFilm(film);
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Создан новый фильм: {}", film);
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка создания пользователя: {}", e.getMessage());
        }
        return film;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }



    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
