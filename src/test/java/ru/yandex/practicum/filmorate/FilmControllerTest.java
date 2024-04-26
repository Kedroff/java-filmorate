package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.controller.FilmController;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {

    @Test
    void testFilmValidationWithEmptyName() {
        Film filmWithEmptyName = new Film();
        filmWithEmptyName.setName("");
        filmWithEmptyName.setDescription("Valid description");
        filmWithEmptyName.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmWithEmptyName.setDuration(120);

        assertThrows(ValidationException.class, () -> FilmController.validateFilm(filmWithEmptyName));
    }

    @Test
    void testFilmValidationWithLongDescription() {
        Film filmWithLongDescription = new Film();
        filmWithLongDescription.setName("Valid Name");
        filmWithLongDescription.setDescription("очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длинное сообщение очень длиннное сообщение очень длинное сообщение очень длинное сообщение");
        filmWithLongDescription.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmWithLongDescription.setDuration(120);

        assertThrows(ValidationException.class, () -> FilmController.validateFilm(filmWithLongDescription));
    }

    @Test
    void testFilmValidationWithInvalidReleaseDate() {
        Film filmWithInvalidReleaseDate = new Film();
        filmWithInvalidReleaseDate.setName("Valid Name");
        filmWithInvalidReleaseDate.setDescription("Valid description");
        filmWithInvalidReleaseDate.setReleaseDate(LocalDate.of(1800, 1, 1));
        filmWithInvalidReleaseDate.setDuration(120);

        assertThrows(ValidationException.class, () -> FilmController.validateFilm(filmWithInvalidReleaseDate));
    }

    @Test
    void testFilmValidationWithNegativeDuration() {
        Film filmWithNegativeDuration = new Film();
        filmWithNegativeDuration.setName("Valid Name");
        filmWithNegativeDuration.setDescription("Valid description");
        filmWithNegativeDuration.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmWithNegativeDuration.setDuration(-120);

        assertThrows(ValidationException.class, () -> FilmController.validateFilm(filmWithNegativeDuration));
    }
}
