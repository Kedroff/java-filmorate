package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FilmValidator {

    private static final LocalDate maxDateRelease = LocalDate.of(1895, 12, 28);
    private static final int descriptionLength = 200;

    public static boolean checkFilm(Film film) throws ValidationException {
        try {
            List<Long> list = new ArrayList<>();
            film.setLikes(list);

            if (film.getName().isBlank()) {
                throw new ValidationException("Поле name не должно быть пустым");
            }

            if (film.getDescription().length() > descriptionLength) {
                throw new ValidationException(String.format("Количество символов поля description не должно быть больше %d. Cейчас длина составляет %d",
                        descriptionLength, film.getDescription().length()));
            }

            if (film.getReleaseDate().isBefore(maxDateRelease)) {
                throw new ValidationException(String.format("Дата релиза не должна быть раньше %s. Указана дата %s",
                        maxDateRelease, film.getReleaseDate()));
            }

            if (film.getDuration() < 0) {
                throw new ValidationException(String.format("Продолжительность фильма не может быть отрицательной. Вы указали длину %d",
                        film.getDuration()));
            }

            return true;
        } catch (NullPointerException e) {
            throw new ValidationException("Поля не должны быть пустыми " + e.getMessage());
        }
    }
}
