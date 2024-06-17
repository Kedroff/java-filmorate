package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GenreDao;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service("FilmDbService")
public class FilmDbService implements FilmServiceInterface {

    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage;

    @Autowired
    public FilmDbService(FilmStorage userStorage) {
        this.filmStorage = userStorage;
    }

    @Override
    public boolean addLike(User user, Film film) {
        return filmStorage.addLike(user, film);
    }

    @Override
    public boolean deleteLike(User user, Film film) {
        return filmStorage.deleteLike(user, film);
    }

    @Override
    public Collection<Film> showFilms(int count) {
        return filmStorage.getFilms();
    }

    @Override
    public Collection<Film> showFilmsAll() {
        return filmStorage.getFilms();
    }


    @Override
    public Film getFilm(int id) {
        if (filmStorage.getFilm(id) != null) {
            return filmStorage.getFilm(id);
        } else {
            throw new NullPointerException(String.format("Фильм с id %s не найден", id));
        }
    }

    @Override
    public Film createFilm(Film film) throws ValidationException {
        return filmStorage.createFilm(film);
    }

    @Override
    public void deleteFilm(long id) {
        filmStorage.deleteFilm(id);
    }

    public Film updateFilm(Film film) {
        Collection<Film> films = this.showFilmsAll();
        if (films.contains(film)) {
            return filmStorage.updateFilm(film);
        } else {
            throw new IllegalArgumentException("Фильм не найден");
        }
    }

    @Override
    public Film getFilm(long id) {
        return filmStorage.getFilm(id);
    }

    @Override
    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public Collection<Film> getFilms(int count) {
        return filmStorage.getFilms(count);
    }

    @Override
    public Mpa getMpa(Optional<Integer> id) {
        return filmStorage.getMpa(id);
    }

    @Override
    public List<Mpa> getMpaAll() {
        return filmStorage.getMpaAll();
    }

    @Override
    public GenreDao getGenres(Optional<Integer> id) {
        return filmStorage.getGenres(id);
    }

    @Override
    public List<GenreDao> getGenresAll() {
        return filmStorage.getGenresAll();
    }

}