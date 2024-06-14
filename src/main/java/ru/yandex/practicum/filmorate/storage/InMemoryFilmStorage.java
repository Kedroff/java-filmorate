package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryFilmStorage implements FilmStorage {

    @Getter
    HashMap<Long, Film> filmHashMap = new HashMap<>();
    int idGen = 1;

    @Override
    public Film createFilm(Film film) throws ValidationException {

        FilmValidator.checkFilm(film);
        film.setId((long) idGen++);
        filmHashMap.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(long id) {
        if (filmHashMap.containsKey(id)) {
            filmHashMap.remove(id);
        } else {
            System.out.println("Фильм с данным id не найден");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmHashMap.containsKey(film.getId())) {
            filmHashMap.put(film.getId(), film);
            return film;
        } else {
            System.out.println("Фильм не найден");
            return null;
        }
    }

    @Override
    public Film getFilm(long id) {
        return filmHashMap.getOrDefault(id, null);
    }

    @Override
    public Collection<Film> getFilms() {
        return filmHashMap.values();
    }

    @Override
    public Collection<Film> getFilms(int count) {
        return List.of();
    }

}