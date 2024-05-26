package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(@RequestBody Film film) throws ValidationException;

    Film update(@RequestBody Film newFilm) throws ValidationException;

    void delete(Long id);

    void validateFilm(Film film) throws ValidationException;

    Film findById(Long id);
}
