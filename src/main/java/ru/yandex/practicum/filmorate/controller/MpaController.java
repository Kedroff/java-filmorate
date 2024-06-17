package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmServiceInterface;

import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/mpa")
@RestController
public class MpaController {

    @Qualifier("FilmDbService")
    private final FilmServiceInterface filmService;

    public MpaController(FilmServiceInterface filmService) {
        this.filmService = filmService;
    }

    @GetMapping(value = "/{id}")
    public Mpa getMpa(@PathVariable(required = false) Optional<Integer> id) {
        if (filmService.getMpa(id) != null) {
            return filmService.getMpa(id);
        } else {
            throw new NullPointerException(String.format("Mpa с id %s не найден", id));
        }
    }

    @GetMapping
    public List<Mpa> getMpaAll() {
        return filmService.getMpaAll();
    }
}