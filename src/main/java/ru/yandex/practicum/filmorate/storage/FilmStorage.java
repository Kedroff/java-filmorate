package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GenreDao;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface FilmStorage {
    public Film createFilm(Film film) throws ValidationException;

    public void deleteFilm(long id);

    public Film updateFilm(Film film);

    public Film getFilm(long id);

    public Collection<Film> getFilms();

    public Collection<Film> getFilms(int count);

    public default boolean addLike(User user, Film film) {
        return false;
    }

    public default boolean deleteLike(User user, Film film) {
        return false;
    }

    public default Mpa getMpa(Optional<Integer> id) {
        return null;
    }

    public default List<Mpa> getMpaAll() {
        return null;
    }

    public default GenreDao getGenres(Optional<Integer> id) {
        return null;
    }

    public default List<GenreDao> getGenresAll() {
        return null;
    }
}