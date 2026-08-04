package ru.yandex.practicum.filmorate.directorTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorRowMapper;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Sql(scripts = {"/schema.sql", "/data.sql"})
class DirectorDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DirectorDbStorage directorStorage;

    @BeforeEach
    void setUp() {
        RowMapper<Director> rowMapper = new DirectorRowMapper();
        directorStorage = new DirectorDbStorage(jdbcTemplate, rowMapper);

        jdbcTemplate.update("DELETE FROM film_directors");
        jdbcTemplate.update("DELETE FROM directors");
        jdbcTemplate.update("ALTER TABLE directors ALTER COLUMN id RESTART WITH 1");
    }

    private Director createDirector(String name) {
        Director director = new Director();
        director.setName(name);
        return directorStorage.create(director);
    }

    private Director createTestDirector() {
        return createDirector("Генри Кавилл");
    }

    @Test
    @DisplayName("Создание режиссера")
    void create_ShouldSaveDirector() {
        Director result = createTestDirector();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Генри Кавилл");
    }

    @Test
    @DisplayName("Поиск режиссера по ID")
    void findById_ShouldReturnDirector() {
        Director saved = createTestDirector();

        Director result = directorStorage.findById(saved.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getName()).isEqualTo("Генри Кавилл");
    }

    @Test
    @DisplayName("Поиск по несуществующему ID, ошибка")
    void findById_NotFound_ShouldThrowException() {
        assertThatThrownBy(() -> directorStorage.findById(999L)).isInstanceOf(NotFoundException.class)
                .hasMessage("Режиссер с id = 999 не найден");
    }

    @Test
    @DisplayName("Получение всех режиссеров")
    void findAll_ShouldReturnAll() {
        createDirector("Генри Кавилл");
        createDirector("Леонардо ДиКаприо");

        Collection<Director> result = directorStorage.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Director::getName)
                .containsExactlyInAnyOrder("Генри Кавилл", "Леонардо ДиКаприо");
    }

    @Test
    @DisplayName("Получение всех, пустой список")
    void findAll_Empty_ShouldReturnEmpty() {
        Collection<Director> result = directorStorage.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Обновление режиссера")
    void update_ShouldUpdateDirector() {
        Director saved = createTestDirector();
        saved.setName("Генри Кавилл - обновлен");

        Director result = directorStorage.update(saved);

        assertThat(result.getName()).isEqualTo("Генри Кавилл - обновлен");

        Director updated = directorStorage.findById(saved.getId());
        assertThat(updated.getName()).isEqualTo("Генри Кавилл - обновлен");
    }

    @Test
    @DisplayName("Обновление несуществующего")
    void update_NotFound_ShouldThrowException() {
        Director nonExistent = new Director();
        nonExistent.setId(999L);
        nonExistent.setName("Несуществующий");

        assertThatThrownBy(() -> directorStorage.update(nonExistent)).isInstanceOf(NotFoundException.class)
                .hasMessage("Режиссер с id = 999 не найден");
    }

    @Test
    @DisplayName("Удаление режиссера")
    void delete_ShouldDeleteDirector() {
        Director saved = createTestDirector();

        directorStorage.delete(saved.getId());

        assertThatThrownBy(() -> directorStorage.findById(saved.getId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Удаление несуществующего - ошибка")
    void delete_NotFound_ShouldThrowException() {
        assertThatThrownBy(() -> directorStorage.delete(999L)).isInstanceOf(NotFoundException.class)
                .hasMessage("Режиссер с id = 999 не найден");
    }

    @Test
    @DisplayName("Удаление с каскадным удалением связей")
    void delete_WithAssociations_ShouldDeleteCascade() {
        Director saved = createTestDirector();

        //Создаём фильм
        jdbcTemplate.update(
                "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)",
                "Человек из стали", "Фильм о Супермене", "2013-01-09", 143, 3
        );
        jdbcTemplate.update(
                "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)",
                1L, saved.getId()
        );

        //Проверяем
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_directors WHERE director_id = ?",
                Integer.class, saved.getId()
        );
        assertThat(count).isEqualTo(1);

        //Удаляем
        directorStorage.delete(saved.getId());

        count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_directors WHERE director_id = ?",
                Integer.class, saved.getId()
        );
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("Автоинкремент ID")
    void create_ShouldGenerateSequentialIds() {
        Director first = createDirector("Генри Кавилл");
        Director second = createDirector("Леонардо ДиКаприо");

        assertThat(first.getId()).isEqualTo(1L);
        assertThat(second.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Сортировка по ID")
    void findAll_ShouldBeSortedById() {
        createDirector("Леонардо ДиКаприо");
        createDirector("Генри Кавилл");
        createDirector("Кристофер Нолан");

        Collection<Director> result = directorStorage.findAll();

        assertThat(result).extracting(Director::getId).containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("Дубликаты имен разрешены")
    void create_DuplicateNames_ShouldSaveBoth() {
        Director first = createDirector("Генри Кавилл");
        Director second = createDirector("Генри Кавилл");

        assertThat(first.getId()).isNotEqualTo(second.getId());
        assertThat(directorStorage.findAll()).hasSize(2);
    }
}