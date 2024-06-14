
package ru.yandex.practicum.filmorate.controller;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmServiceInterface;
import ru.yandex.practicum.filmorate.service.UserServiceInterface;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {

    static final String pathForAddDeleteLike = "/{id}/like/{userId}";

    @Qualifier("FilmDbService")
    FilmServiceInterface filmService;
    @Qualifier("UserDbService")
    UserServiceInterface userService;

    @Autowired
    public FilmController(FilmServiceInterface filmService, UserServiceInterface userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.showFilmsAll();
    }

    @GetMapping(value = "/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getFilms(@RequestParam(required = false) Integer count) {
        if (count == null) {
            return filmService.showFilms(10);
        } else {
            return filmService.showFilms(count);
        }
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        FilmValidator.checkFilm(film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@NotNull @RequestBody Film film) throws ValidationException {
        FilmValidator.checkFilm(film);
        if (filmService.updateFilm(film) != null) {
            return filmService.updateFilm(film);
        } else {
            throw new NullPointerException("Фильм c id " + film.getId() + " не найден");
        }

    }

    @PutMapping(value = pathForAddDeleteLike)
    public void addLikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        Film film = filmService.getFilm(id);
        User user = userService.getUser(userId);
        filmService.addLike(user, film);
    }

    @DeleteMapping(value = pathForAddDeleteLike)
    public void deleteLikeFilm(@PathVariable int id, @PathVariable int userId) {
        Film film = filmService.getFilm(id);
        User user = userService.getUser(userId);
        filmService.deleteLike(user, film);
    }


}