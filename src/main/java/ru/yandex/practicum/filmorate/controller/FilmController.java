package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        try {
            if (film.getName() == null || film.getReleaseDate() == null || film.getDuration() == null) {
                throw new ValidationException("Отсутствуют обязательные поля");
            }
            validateFilm(film);
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Создан новый фильм: {}", film);
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка создания фильма: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка создания фильма: " + e.getMessage());
        } catch (Exception e) {
            log.error("Внутренняя ошибка сервера: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }


    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        try {
            if (newFilm.getId() == null) {
                log.error("Id должен быть указан");
                throw new ValidationException("Id должен быть указан");
            }

            try {
                if (films.containsKey(newFilm.getId())) {

                    validateFilm(newFilm);
                    Film oldFilm = films.get(newFilm.getId());

                    oldFilm.setName(newFilm.getName());
                    oldFilm.setDescription(newFilm.getDescription());
                    oldFilm.setReleaseDate(newFilm.getReleaseDate());
                    oldFilm.setDuration(newFilm.getDuration());
                    log.info("Фильм успешно изменен");
                    return oldFilm;
                } else {
                    log.error("Фильма с таким id не существует");
                }
            } catch (ValidationException e) {
                log.error("Ошибка: {}", e.getMessage());
            }
        } catch (ValidationException e) {
            log.error("Ошибка обновления фильма: {}" + e.getMessage());
        }
        return null;
    }

    public static void validateFilm(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
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
