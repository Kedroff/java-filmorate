package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Long filmId, Long userId) throws ValidationException {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            throw new ValidationException("Film not found");
        }
        film.getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) throws ValidationException {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            throw new ValidationException("Film not found");
        }
        film.getLikes().remove(userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
        }
        return film;
    }

    public Film create(@RequestBody Film film) {
        try {
            validateFilm(film);
            film.setId(getNextId());
            filmStorage.create(film);
            log.info("Создан новый фильм: {}", film);
            return film;
        } catch (ValidationException e) {
            String errorMessage = "Ошибка создания фильма: ";
            log.error(errorMessage + " {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage + e.getMessage());
        } catch (Exception e) {
            String errorMessage = "Внутренняя ошибка сервера: ";
            log.error(errorMessage + " {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage + e.getMessage());
        }
    }

    public Film update(@RequestBody Film newFilm) {
        try {
            validateFilm(newFilm);
            return filmStorage.update(newFilm);
        } catch (ValidationException e) {
            String errorMessage = "Ошибка валидации: ";
            log.error(errorMessage + " {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage + e.getMessage());
        } catch (Exception e) {
            String errorMessage = "Ошибка обновления фильма: ";
            log.error(errorMessage + " {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage + e.getMessage());
        }
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Имя не должно быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Описание не должно быть больше 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть больше 0");
        }
    }

    private long getNextId() {
        long currentMaxId = filmStorage.findAll().stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
