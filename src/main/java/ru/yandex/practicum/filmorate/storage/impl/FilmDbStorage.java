package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GenreDao;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;


@Component("FilmDbStorage")
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) throws ValidationException {
        List<GenreDao> genres = new ArrayList<>();
        List<Long> likes = new ArrayList<>();
        String sql = "insert into films (name, description, release_date, duration) values(?,?,?,?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration());
        Long filmId = jdbcTemplate.queryForObject(
                "SELECT film_id FROM films ORDER BY film_id DESC LIMIT 1",
                Long.class);
        film.setId(filmId);
        if (film.getGenres() == null) {
            film.setGenres(genres);
        }
        if (film.getLikes() == null) {
            film.setLikes(likes);
        }
        this.fillGenre(film);
        this.fillMpa(film);
        return film;
    }

    private List<Integer> getAllGenres() {
        List<Integer> genres = new ArrayList<>();
        String sql = "select genre_id from genres";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            GenreDao genre = new GenreDao();
            genre.setId(sqlRowSet.getInt("genre_id"));
            genres.add(genre.getId());
        }
        return genres;
    }

    private List<Integer> getAllMpa() {
        List<Integer> mpaAll = new ArrayList<>();
        String sql = "select id from mpa";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            Mpa mpa = new Mpa();
            mpa.setId(sqlRowSet.getInt("id"));
            mpaAll.add(mpa.getId());
        }
        return mpaAll;

    }

    private void fillGenre(Film film) throws ValidationException {
        List<Integer> genresAll = this.getAllGenres();
        for (GenreDao genre : film.getGenres()) {
            if (genresAll.contains(genre.getId())) {
                String sql = "insert into genre_films (film_id, genre_id) values(?,?)";
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            } else {
                throw new ValidationException("Genre c id " + genre.getId() + " не найдено");
            }
        }
    }

    private void fillMpa(Film film) throws ValidationException {
        if (film.getMpa() != null) {
            List<Integer> mpaAll = this.getAllMpa();
            if (mpaAll.contains(film.getMpa().getId())) {
                String sql = "insert into mpa_films (film_id, mpa_id) values(?,?)";
                jdbcTemplate.update(sql, film.getId(), film.getMpa().getId());
            } else {
                throw new ValidationException("Mpa c id " + film.getMpa().getId() + " не найдено");
            }
        }
    }

    private List<Long> getLikesUsers(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new ArrayList<>());
        }
        String sql = "select users_id from film_liked where films_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, film.getId());
        while (sqlRowSet.next()) {
            film.getLikes().add(sqlRowSet.getLong("users_id"));
        }
        return film.getLikes();
    }

    private Mpa getMpaFilm(Film film) {
        Mpa mpa = new Mpa();
        String sql = "select * from mpa_films as mf inner join mpa as m on m.id = mf.mpa_id where mf.film_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, film.getId());
        while (sqlRowSet.next()) {
            mpa.setId(sqlRowSet.getInt("mpa_id"));
            mpa.setName(sqlRowSet.getString("name"));
        }
        return mpa;
    }

    @Override
    public void deleteFilm(long id) {
        String sql = "delete from films where film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "update films set name = ?, description = ?, release_date = ?, duration = ? where film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getId());
        this.getLikesUsers(film);
        return film;
    }

    @Override
    public Film getFilm(long id) {
        String sql = "select * from films where film_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        Film film = Film.builder().build();
        while (rowSet.next()) {
            film.setId(rowSet.getInt("film_id"));
            film.setName(rowSet.getString("name"));
            film.setDescription(rowSet.getString("description"));
            film.setDuration(rowSet.getInt("duration"));
            film.setReleaseDate(rowSet.getDate("release_date").toLocalDate());
            film.setGenres(this.getGenresFromFilm(id));
            film.setLikes(this.getLikesUsers(film));
            film.setMpa(this.getMpaFilm(film));
            return film;
        }
        return null;
    }

    private List<GenreDao> getGenresFromFilm(long id) {
        Set<GenreDao> genreDaoSet = new HashSet<>();
        String sql = "select * from genre_films as gf inner join genres as g on g.genre_id=gf.genre_id where film_id = ?";
        List<GenreDao> genreDaos = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        while (sqlRowSet.next()) {
            GenreDao genreDao = new GenreDao();
            genreDao.setId(sqlRowSet.getInt("genre_id"));
            genreDao.setName(sqlRowSet.getString("name"));
            genreDaoSet.add(genreDao);
        }
        boolean ifAdd = genreDaos.addAll(genreDaoSet);
        return genreDaos;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> films = new ArrayList<>();
        String sql = "select * from films as f left join film_liked as fl on fl.films_id = f.film_id";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            Film film = Film.builder()
                    .id(rowSet.getInt("film_id"))
                    .name(rowSet.getString("name"))
                    .description(rowSet.getString("description"))
                    .releaseDate(rowSet.getDate("release_date").toLocalDate())
                    .duration(rowSet.getInt("duration"))
                    .build();
            film.setGenres(this.getGenresFromFilm(film.getId()));
            film.setMpa(this.getMpaFilm(film));
            this.getLikesUsers(film);
            films.add(film);
        }
        return films;
    }

    @Override
    public List<Film> getFilms(int count) {
        List<Film> films = new ArrayList<>();
        String sql = "select * from films as f left join film_liked as fl on fl.films_id = f.film_id ";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next() && films.size() != count) {
            Film film = Film.builder()
                    .id(rowSet.getInt("film_id"))
                    .name(rowSet.getString("name"))
                    .description(rowSet.getString("description"))
                    .duration(rowSet.getInt("duration"))
                    .releaseDate(rowSet.getDate("release_date").toLocalDate())
                    .build();
            film.setGenres(this.getGenresFromFilm(film.getId()));
            film.setMpa(this.getMpaFilm(film));
            film.setLikes(this.getLikesUsers(film));
            films.add(film);
        }
        films.sort((Comparator.comparingInt(film -> -film.getLikes().size())));
        return films;
    }

    @Override
    public boolean addLike(User user, Film film) {
        String sql = "insert into film_liked (films_id, users_id) values (?,?)";
        int edit = jdbcTemplate.update(sql, film.getId(), user.getId());
        user.getFilmIdLiked().add(film.getId());
        film.getLikes().add(user.getId());
        return edit == 1;
    }

    @Override
    public boolean deleteLike(User user, Film film) {
        String sql = "delete from film_liked where films_id = ? and users_id = ?";
        int edit = jdbcTemplate.update(sql, film.getId(), user.getId());
        user.getFilmIdLiked().remove(film.getId());
        film.getLikes().remove(user.getId());
        return edit == 1;
    }

    @Override
    public GenreDao getGenres(Optional<Integer> id) { // доработать
        GenreDao genreDao = new GenreDao();
        SqlRowSet sqlRowSet;
        String sql = "";
        if (id.isEmpty()) {
            sql = "select * from genres";
            sqlRowSet = jdbcTemplate.queryForRowSet(sql);

        } else {
            sql = "select * from genres where genre_id = ?";
            sqlRowSet = jdbcTemplate.queryForRowSet(sql, id.get());
        }
        while (sqlRowSet.next()) {
            genreDao.setId(sqlRowSet.getInt("genre_id"));
            genreDao.setName(sqlRowSet.getString("name"));
            return genreDao;
        }
        return null;
    }

    @Override
    public List<GenreDao> getGenresAll() { // доработать
        List<GenreDao> genres = new ArrayList<>();
        String sql = "select * from genres";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            GenreDao genreDao = new GenreDao();
            genreDao.setId(sqlRowSet.getInt("genre_id"));
            genreDao.setName(sqlRowSet.getString("name"));
            genres.add(genreDao);
        }
        return genres;
    }

    @Override
    public Mpa getMpa(Optional<Integer> id) { // доработать
        Mpa mpa = new Mpa();
        String sql = "select * from mpa where id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id.get());
        while (sqlRowSet.next()) {
            mpa.setId(sqlRowSet.getInt("id"));
            mpa.setName(sqlRowSet.getString("name"));
            return mpa;
        }
        return null;
    }

    @Override
    public List<Mpa> getMpaAll() {
        List<Mpa> mpas = new ArrayList<>();
        String sql = "SELECT * FROM mpa ORDER BY id ASC";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while (sqlRowSet.next()) {
            Mpa mpa = new Mpa();
            mpa.setId(sqlRowSet.getInt("id"));
            mpa.setName(sqlRowSet.getString("name"));
            mpas.add(mpa);
        }
        return mpas;
    }



}