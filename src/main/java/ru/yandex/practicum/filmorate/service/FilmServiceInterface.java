package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GenreDao;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmServiceInterface extends FilmStorage {

    public boolean addLike(User user, Film film);

    public boolean deleteLike(User user, Film film);

    public Collection<Film> showFilms(int count);

    public Collection<Film> showFilmsAll();

    public Film getFilm(int id);

    public default GenreDao getGenres(Optional<Integer> id) {
        return null;
    }

    public default List<GenreDao> getGenresAll() {
        return null;
    }

    public default Mpa getMpa(Optional<Integer> id) {
        return null;
    }

    public default List<Mpa> getMpaAll() {
        return null;
    }
}