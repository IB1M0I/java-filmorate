package ru.yandex.practicum.filmorate.filmTest;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmRowMapper;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Collection;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class})
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    @Test
    public void createFilmTest(){
        Film film = Film.builder()
                .name("Фильм")
                .description("Описание")
                .releaseDate(LocalDate.now())
                .duration(120)
                .genre(new LinkedHashSet<>(Set.of(new Genre(1))))
                .mpa(new MpaRating(1))
                .build();
        Film filmSave = filmStorage.addFilm(film);

        Film filmFind = filmStorage.findById(filmSave.getId());

        Assertions.assertThat(filmFind).isNotNull();
        Assertions.assertThat(filmFind.getId()).isEqualTo(filmSave.getId());
        Assertions.assertThat(filmFind.getName()).isEqualTo(film.getName());
        Assertions.assertThat(filmFind.getReleaseDate()).isEqualTo(film.getReleaseDate());
        Assertions.assertThat(filmFind.getDuration()).isEqualTo(film.getDuration());
        Assertions.assertThat(filmFind.getMpa()).isEqualTo(film.getMpa());
    }

    @Test
    public void testFindFilmById(){
        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genre(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.now())
                .genre(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .build();

        filmStorage.addFilm(film1);
        Film filmSave = filmStorage.addFilm(film2);

        Film findFilm = filmStorage.getLikesAndGenresByFilmId(filmStorage.findById(filmSave.getId()));

        Assertions.assertThat(findFilm).isNotNull();
        Assertions.assertThat(findFilm.getId()).isEqualTo(filmSave.getId());
        Assertions.assertThat(findFilm.getName()).isEqualTo("Фильм2");
        Assertions.assertThat(findFilm.getDescription()).isEqualTo("Описание2");
        Assertions.assertThat(findFilm.getDuration()).isEqualTo(200);
        Assertions.assertThat(findFilm.getReleaseDate()).isEqualTo(filmSave.getReleaseDate());

        Assertions.assertThat(findFilm.getMpa()).isNotNull();
        Assertions.assertThat(findFilm.getMpa().getId()).isEqualTo(1);

        Assertions.assertThat(findFilm.getGenre()).isNotEmpty();
        Assertions.assertThat(findFilm.getGenre().iterator().next().getId()).isEqualTo(4);

    }

    @Test
    public void testFindUserById_NotFound(){
        Film film = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genre(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();

        filmStorage.addFilm(film);

        Assertions.assertThatThrownBy(() -> filmStorage.findById(33))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    public void testUpdateFilm(){
        Film film = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genre(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        Film saveFilm = filmStorage.addFilm(film);

        Film updateFilm = Film.builder()
                .id(film.getId())
                .name("Update Film")
                .description("Update description")
                .duration(200)
                .genre(new LinkedHashSet<>(Set.of(new Genre(1),new Genre(2))))
                .mpa(new MpaRating(1))
                .releaseDate(LocalDate.of(2000,1,1))
                .build();

        filmStorage.updateFilm(updateFilm);
        Film respone = filmStorage.getLikesAndGenresByFilmId(filmStorage.findById(film.getId()));
        Assertions.assertThat(respone).isNotNull();
        Assertions.assertThat(respone.getId()).isEqualTo(film.getId());
        Assertions.assertThat(respone.getName()).isEqualTo("Update Film");
        Assertions.assertThat(respone.getReleaseDate()).isEqualTo(LocalDate.of(2000,1,1));
        Assertions.assertThat(respone.getDuration()).isEqualTo(200);
        Assertions.assertThat(respone.getGenre()).isNotNull();
        Assertions.assertThat(respone.getGenre()).isEqualTo(Set.of(new Genre(1,"Комедия"),new Genre(2,"Драма")));
        Assertions.assertThat(respone.getMpa()).isNotNull();
        Assertions.assertThat(respone.getMpa().getId()).isEqualTo(1);
    }

    @Test
    public void testFindAllFilms(){
        Film film1 = Film.builder()
                .name("Фильм")
                .description("Описание")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genre(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();
        Film film2 = Film.builder()
                .name("Фильм2")
                .description("Описание2")
                .duration(200)
                .releaseDate(LocalDate.now())
                .genre(new LinkedHashSet<>(Set.of(new Genre(4))))
                .mpa(new MpaRating(1))
                .build();
        Film film3 = Film.builder()
                .name("Фильм3")
                .description("Описание3")
                .duration(100)
                .releaseDate(LocalDate.now())
                .genre(new LinkedHashSet<>(Set.of(new Genre(2))))
                .mpa(new MpaRating(3))
                .build();

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);

        Collection<Film> films = filmStorage.findAll();

        Assertions.assertThat(films).isNotNull();
        Assertions.assertThat(films).hasSize(3);

    }
}
