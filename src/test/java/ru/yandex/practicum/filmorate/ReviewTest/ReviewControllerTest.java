package ru.yandex.practicum.filmorate.ReviewTest;

/*import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.storage.review.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.storage.review.dto.UpdateReviewRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private NewReviewRequest newReviewRequest;

    private UpdateReviewRequest updateReviewRequest;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM review_likes");
        jdbcTemplate.update("DELETE FROM reviews");
        jdbcTemplate.update("DELETE FROM likes_movies");
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM movie_genres");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "test@test.com", "testuser", "Test", "2000-01-01");
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)",
                "Film", "Desc", "2020-01-01", 120, 1);

        newReviewRequest = new NewReviewRequest();
        newReviewRequest.setContent("Отличный фильм!");
        newReviewRequest.setIsPositive(true);
        newReviewRequest.setUserId(1L);
        newReviewRequest.setFilmId(1L);

        updateReviewRequest = new UpdateReviewRequest();
        updateReviewRequest.setReviewId(1L);
        updateReviewRequest.setContent("Обновленный отзыв");
        updateReviewRequest.setIsPositive(false);
    }

    @Test
    @DisplayName("Добавляем новый отзыв")
    void addReview_ShouldReturnCreatedReview() throws Exception {
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReviewRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId").exists())
                .andExpect(jsonPath("$.content").value("Отличный фильм!"))
                .andExpect(jsonPath("$.isPositive").value(true))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.filmId").value(1))
                .andExpect(jsonPath("$.useful").value(0));
    }

    @Test
    @DisplayName("Обновление отзыва")
    void updateReview_ShouldReturnUpdatedReview() throws Exception {
        String response = mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReviewRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reviewId = objectMapper.readTree(response).get("reviewId").asLong();
        updateReviewRequest.setReviewId(reviewId);

        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(reviewId))
                .andExpect(jsonPath("$.content").value("Обновленный отзыв"))
                .andExpect(jsonPath("$.isPositive").value(false));
    }

    @Test
    @DisplayName("Удаляем отзыв")
    void deleteReview_ShouldReturnNoContent() throws Exception {
        // Создаем отзыв
        String response = mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReviewRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reviewId = objectMapper.readTree(response).get("reviewId").asLong();

        mockMvc.perform(delete("/reviews/{id}", reviewId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Получаем все отзывы")
    void findAll_ShouldReturnReviews() throws Exception {
        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Добавляем лайк к отзыву")
    void addLike_ShouldIncreaseUsefulRating() throws Exception {
        String response = mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReviewRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reviewId = objectMapper.readTree(response).get("reviewId").asLong();

        mockMvc.perform(put("/reviews/{id}/like/{userId}", reviewId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(1));
    }

    @Test
    @DisplayName("Добавляем дизлайк к отзыву")
    void addDislike_ShouldDecreaseUsefulRating() throws Exception {
        String response = mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReviewRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reviewId = objectMapper.readTree(response).get("reviewId").asLong();

        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", reviewId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(-1));
    }

    @Test
    @DisplayName("Удаляем лайка с отзыва")
    void deleteLike_ShouldUpdateUsefulRating() throws Exception {
        String response = mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReviewRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reviewId = objectMapper.readTree(response).get("reviewId").asLong();

        // Сначала ставим лайк
        mockMvc.perform(put("/reviews/{id}/like/{userId}", reviewId, 1L))
                .andExpect(status().isOk());

        // Затем удаляем лайк
        mockMvc.perform(delete("/reviews/{id}/like/{userId}", reviewId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(0));
    }

    @Test
    @DisplayName("Удаляем дизлайк с отзыва")
    void deleteDislike_ShouldUpdateUsefulRating() throws Exception {
        String response = mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReviewRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reviewId = objectMapper.readTree(response).get("reviewId").asLong();

        // Сначала ставим дизлайк
        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", reviewId, 1L))
                .andExpect(status().isOk());

        // Затем удаляем дизлайк
        mockMvc.perform(delete("/reviews/{id}/dislike/{userId}", reviewId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.useful").value(0));
    }

}*/
