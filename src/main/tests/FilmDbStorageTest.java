package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GenreDao;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testCreateFilm() throws ValidationException {

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film = Film.builder()
                .name("test")
                .description("testDescription")
                .duration(123)
                .releaseDate(LocalDate.of(1988, 3, 23))
                .genres(new ArrayList<>())
                .mpa(mpa)
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.createFilm(film);

        assertThat(filmDbStorage.getFilm(film.getId()))
                .isNotNull()
                .isEqualTo(film);
    }

    @Test
    public void testDeleteFilm() throws ValidationException {
        Film film = Film.builder()
                .name("test")
                .description("testDescription")
                .duration(123)
                .releaseDate(LocalDate.of(1988, 3, 23))
                .genres(new ArrayList<>())
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.createFilm(film);
        filmDbStorage.deleteFilm(film.getId());

        assertThat(filmDbStorage.getFilm(film.getId()))
                .isNull();
    }

    @Test
    public void testUpdateFilm() throws ValidationException {

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film = Film.builder()
                .name("test")
                .description("testDescription")
                .duration(123)
                .releaseDate(LocalDate.of(1988, 3, 23))
                .mpa(mpa)
                .genres(new ArrayList<>())
                .build();


        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);

        filmDbStorage.createFilm(film);

        Film film1 = Film.builder()
                .id(film.getId())
                .name("testNew")
                .description("testDescriptionNew")
                .duration(123)
                .genres(new ArrayList<>())
                .mpa(mpa)
                .releaseDate(LocalDate.of(1988, 3, 23))
                .build();

        filmDbStorage.updateFilm(film1);

        Film film2 = filmDbStorage.getFilm(film.getId());

        assertThat(film2)
                .isNotNull()
                .isEqualTo(film1);
    }

    @Test
    public void testGetFilm() throws ValidationException {

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film1 = Film.builder()
                .name("testNew")
                .description("testDescriptionNew")
                .duration(123)
                .releaseDate(LocalDate.of(1988, 3, 23))
                .mpa(mpa)
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);

        filmDbStorage.createFilm(film1);

        Film film2 = filmDbStorage.getFilm(film1.getId());

        assertThat(film2)
                .isNotNull()
                .isEqualTo(film1);
    }

    @Test
    public void testGetFilms() throws ValidationException {

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film1 = Film.builder()
                .name("testNew")
                .description("testDescriptionNew")
                .duration(123)
                .genres(new ArrayList<>())
                .mpa(mpa)
                .releaseDate(LocalDate.of(1988, 3, 23))
                .build();
        Film film2 = Film.builder()
                .name("test")
                .description("testDescription")
                .duration(123)
                .genres(new ArrayList<>())
                .mpa(mpa)
                .releaseDate(LocalDate.of(1988, 3, 23))
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);

        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);

        List<Film> films = filmDbStorage.getFilms();
        List<Film> filmsManual = new ArrayList<>();
        filmsManual.add(film1);
        filmsManual.add(film2);

        assertThat(films)
                .isNotNull()
                .isEqualTo(filmsManual);
    }

    @Test
    public void testGetFilmsCount() throws ValidationException {

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        User newUser1 = User.builder()
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(LocalDate.of(1988, 3, 23)))
                .friends(new ArrayList<>())
                .build();

        User newUser2 = User.builder()
                .name("Ivan Petrov2")
                .email("user@email.ru")
                .login("vanya1234")
                .birthday(java.sql.Date.valueOf(LocalDate.of(1988, 3, 23)))
                .friends(new ArrayList<>())
                .build();

        Film film1 = Film.builder()
                .name("testNew")
                .description("testDescriptionNew")
                .duration(123)
                .genres(new ArrayList<>())
                .releaseDate(LocalDate.of(1988, 3, 23))
                .mpa(mpa)
                .build();

        Film film2 = Film.builder()
                .name("test")
                .description("testDescription")
                .duration(123)
                .genres(new ArrayList<>())
                .mpa(mpa)
                .releaseDate(LocalDate.of(1988, 3, 23))
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);

        userDbStorage.createUser(newUser1);
        userDbStorage.createUser(newUser2);

        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);

        filmDbStorage.addLike(newUser1, film2);
        filmDbStorage.addLike(newUser2, film2);

        filmDbStorage.addLike(newUser1, film1);


        List<Film> films = filmDbStorage.getFilms(2);
        List<Film> filmsManual = new ArrayList<>();
        filmsManual.add(film2);
        filmsManual.add(film1);


        assertThat(films)
                .isNotNull()
                .isEqualTo(filmsManual);
    }

    @Test
    public void testAddLike() throws ValidationException {
        Film film1 = Film.builder()
                .name("testNew")
                .description("testDescriptionNew")
                .duration(123)
                .genres(new ArrayList<>())
                .releaseDate(LocalDate.of(1988, 3, 23))
                .build();
        User newUser = User.builder()
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(LocalDate.of(1988, 3, 23)))
                .friends(new ArrayList<>())
                .build();
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);

        userDbStorage.createUser(newUser);
        filmDbStorage.createFilm(film1);

        filmDbStorage.addLike(newUser, film1);

        List<Long> likes = new ArrayList<>();
        likes.add(newUser.getId());

        assertThat(likes)
                .isNotNull()
                .isEqualTo(film1.getLikes());
    }

    @Test
    public void testDeleteLike() throws ValidationException {
        Film film1 = Film.builder()
                .name("testNew")
                .description("testDescriptionNew")
                .duration(123)
                .genres(new ArrayList<>())
                .releaseDate(LocalDate.of(1988, 3, 23))
                .build();
        User newUser = User.builder()
                .name("Ivan Petrov")
                .email("user@email.ru")
                .login("vanya123")
                .birthday(java.sql.Date.valueOf(LocalDate.of(1988, 3, 23)))
                .friends(new ArrayList<>())
                .build();
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);

        userDbStorage.createUser(newUser);
        filmDbStorage.createFilm(film1);

        filmDbStorage.deleteLike(newUser, film1);

        List<Long> likes = new ArrayList<>();

        assertThat(likes)
                .isNotNull()
                .isEqualTo(film1.getLikes());
    }

    @Test
    public void testGetMpa() throws ValidationException {

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");


        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);

        Mpa mpa1 = filmDbStorage.getMpa(Optional.of(mpa.getId()));

        assertThat(mpa1)
                .isNotNull()
                .isEqualTo(mpa);
    }

    @Test
    public void testGetMpaAll() {

        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("G");

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("PG");

        Mpa mpa3 = new Mpa();
        mpa3.setId(3);
        mpa3.setName("PG-13");

        Mpa mpa4 = new Mpa();
        mpa4.setId(4);
        mpa4.setName("R");

        Mpa mpa5 = new Mpa();
        mpa5.setId(5);
        mpa5.setName("NC-17");

        List<Mpa> mpaList = new ArrayList<>();
        mpaList.add(mpa1);
        mpaList.add(mpa2);
        mpaList.add(mpa3);
        mpaList.add(mpa4);
        mpaList.add(mpa5);

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);

        List<Mpa> mpas = filmDbStorage.getMpaAll();

        assertThat(mpas)
                .isNotNull()
                .isEqualTo(mpaList);
    }

    @Test
    public void getGenresFromId() {
        GenreDao genreDao1 = new GenreDao();
        genreDao1.setId(1);
        genreDao1.setName("Комедия");

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        GenreDao genres = filmDbStorage.getGenres(Optional.of(genreDao1.getId()));

        assertThat(genres)
                .isNotNull()
                .isEqualTo(genreDao1);
    }

    @Test
    public void getGenresAll() {
        GenreDao genreDao1 = new GenreDao();
        genreDao1.setId(1);
        genreDao1.setName("Комедия");
        GenreDao genreDao2 = new GenreDao();
        genreDao2.setId(2);
        genreDao2.setName("Драма");
        GenreDao genreDao3 = new GenreDao();
        genreDao3.setId(3);
        genreDao3.setName("Мультфильм");
        GenreDao genreDao4 = new GenreDao();
        genreDao4.setId(4);
        genreDao4.setName("Триллер");
        GenreDao genreDao5 = new GenreDao();
        genreDao5.setId(5);
        genreDao5.setName("Документальный");
        GenreDao genreDao6 = new GenreDao();
        genreDao6.setId(6);
        genreDao6.setName("Боевик");

        List<GenreDao> genreDaos = new ArrayList<>();
        genreDaos.add(genreDao1);
        genreDaos.add(genreDao2);
        genreDaos.add(genreDao3);
        genreDaos.add(genreDao4);
        genreDaos.add(genreDao5);
        genreDaos.add(genreDao6);

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        List<GenreDao> genres = filmDbStorage.getGenresAll();

        assertThat(genres)
                .isNotNull()
                .isEqualTo(genreDaos);

    }
}
