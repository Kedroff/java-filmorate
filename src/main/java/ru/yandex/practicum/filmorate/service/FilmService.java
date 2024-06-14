package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
@Getter
public class FilmService implements FilmServiceInterface {

    @Autowired
    final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public Film createFilm(Film film) throws ValidationException {
        return filmStorage.createFilm(film);
    }

    @Override
    public void deleteFilm(long id) {
        filmStorage.deleteFilm(id);
    }

    @Override
    public Film updateFilm(Film film) {
        Collection<Film> films = this.getFilms();
        for (Film film1 : films) {
            if (film1.getId() == film.getId()) {
                return filmStorage.updateFilm(film);
            }
        }
        throw new IllegalArgumentException("Фильм с id " + film.getId() + " не найден");
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
    public boolean addLike(User user, Film film) {
        if (!user.getFilmIdLiked().contains(film.getId())) {
            filmStorage.addLike(user, film);
            return true;
        } else {
            System.out.println("Лайк от пользователя " + user.getLogin() + " уже поставлен!");
            return false;
        }
    }

    @Override
    public boolean deleteLike(User user, Film film) {
        if (!user.getFilmIdLiked().contains(film.getId())) {
            user.getFilmIdLiked().remove(film.getId());
            film.getLikes().remove(user.getId());
            return true;
        } else {
            System.out.println("Лайк от пользователя " + user.getLogin() + " не удален!");
            return false;
        }
    }

    @Override
    public Collection<Film> showFilms(int count) {
        return filmStorage.getFilms(count);
    }

    @Override
    public Collection<Film> showFilmsAll() {
        return filmStorage.getFilms();
    }

    @Override
    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    @Override
    public GenreDao getGenres(Optional<Integer> id) {
        return filmStorage.getGenres(id);
    }

    @Override
    public List<GenreDao> getGenresAll() {
        return filmStorage.getGenresAll();
    }

    @Override
    public Mpa getMpa(Optional<Integer> id) {
        return filmStorage.getMpa(id);
    }

    @Override
    public List<Mpa> getMpaAll() {
        return filmStorage.getMpaAll();
    }
}