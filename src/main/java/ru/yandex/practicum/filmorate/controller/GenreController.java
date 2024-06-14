package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.GenreDao;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

@RequestMapping("/genres")
@RestController
@Component
public class GenreController {

    @Qualifier("FilmDbService")
    private final FilmService filmService;

    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping(value = "/{id}")
    public GenreDao getGenres(@PathVariable(required = false) Optional<Integer> id) {
        if (filmService.getGenres(id) != null) {
            return filmService.getGenres(id);
        } else {
            throw new NullPointerException("Жанр с id " + id + " не найден");
        }
    }

    @GetMapping
    public List<GenreDao> getGenresAll() {
        return filmService.getGenresAll();
    }
}