package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.testUtils.DataGenerator;
import ru.yandex.practicum.filmorate.testUtils.FilmGenerator;
import ru.yandex.practicum.filmorate.testUtils.GsonConverter;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTests {

    @Autowired
    private MockMvc mockMvc;
    private final String urlTemplate = "/films";

    @Test
    public void GetAllFilms_executeRequest_ShouldReturn200Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        // Act
        mockMvc.perform(post(urlTemplate)
                .content(jsonFilm)
                .contentType(MediaType.APPLICATION_JSON));

        var result = mockMvc.perform(get(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        // Assert
        var actualResponse = result.getResponse().getContentAsString();
        var actualStatusCode = result.getResponse().getStatus();

        film.setId(1);
        Assertions.assertEquals("[" + GsonConverter.convertObjectToJson(film) + "]", actualResponse);
        Assertions.assertEquals(200, actualStatusCode);
    }

    @Test
    public void CreateFilm_withAllParams_ShouldReturn200Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        // Act
        var result = mockMvc
                .perform(post(urlTemplate)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualResponse = result.getResponse().getContentAsString();
        var actualStatusCode = result.getResponse().getStatus();

        film.setId(1);

        Assertions.assertEquals(GsonConverter.convertObjectToJson(film), actualResponse);
        Assertions.assertEquals(200, actualStatusCode);
    }

    @Test
    public void CreateFilm_whenNameIsAlreadyExist_ShouldReturn400Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        film.setId(2);
        var newJsonFilm = GsonConverter.convertObjectToJson(film);

        mockMvc.perform(post(urlTemplate)
                .content(jsonFilm)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(post(urlTemplate)
                        .content(newJsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void CreateFilm_withEmptyName_ShouldReturn400Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        film.setName("");
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        // Act
        var result = mockMvc
                .perform(post(urlTemplate)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void CreateFilm_withNullName_ShouldReturn400Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        film.setName(null);
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        // Act
        var result = mockMvc
                .perform(post(urlTemplate)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void CreateFilm_withTooLongDescription_ShouldReturn400Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        film.setDescription(DataGenerator.generateStringWithLength(201));
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        // Act
        var result = mockMvc
                .perform(post(urlTemplate)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void CreateFilm_withFilmDateLessThanRequired_ShouldReturn400Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        film.setReleaseDate(LocalDate.parse(Film.getMinFilmDate()));
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        // Act
        var result = mockMvc
                .perform(post(urlTemplate)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void CreateFilm_withNegativeFilmDuration_ShouldReturn400Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        film.setDuration(-1);
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        // Act
        var result = mockMvc
                .perform(post(urlTemplate)
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void UpdateFilm_withCorrectParams_ShouldReturn200Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        film.setName("NewName");
        film.setDuration(film.getDuration() - 10);
        film.setDescription("NewDesc");
        film.setReleaseDate(LocalDate.now().minusMonths(1));
        film.setId(2);
        var newJsonFilm = GsonConverter.convertObjectToJson(film);

        mockMvc.perform(post(urlTemplate)
                .content(jsonFilm)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(post(urlTemplate)
                        .content(newJsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();
        var actualResponse = result.getResponse().getContentAsString();

        Assertions.assertEquals(200, actualStatusCode);
        Assertions.assertEquals(newJsonFilm, actualResponse);
    }

    @Test
    public void UpdateFilm_withEmptyName_ShouldReturn400Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        film.setName("");
        var newJsonFilm = GsonConverter.convertObjectToJson(film);

        mockMvc.perform(post(urlTemplate)
                .content(jsonFilm)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(post(urlTemplate)
                        .content(newJsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void UpdateFilm_withNullName_ShouldReturn400Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        film.setName(null);
        var newJsonFilm = GsonConverter.convertObjectToJson(film);

        mockMvc.perform(post(urlTemplate)
                .content(jsonFilm)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(post(urlTemplate)
                        .content(newJsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void UpdateFilm_withTooLongDescription_ShouldReturn400Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        film.setDescription(DataGenerator.generateStringWithLength(201));
        var newJsonFilm = GsonConverter.convertObjectToJson(film);

        mockMvc.perform(post(urlTemplate)
                .content(jsonFilm)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(post(urlTemplate)
                        .content(newJsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void UpdateFilm_withFilmDateLessThanRequires_ShouldReturn400Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        film.setReleaseDate(LocalDate.parse(Film.getMinFilmDate()).minusDays(1));
        var newJsonFilm = GsonConverter.convertObjectToJson(film);

        mockMvc.perform(post(urlTemplate)
                .content(jsonFilm)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(post(urlTemplate)
                        .content(newJsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void UpdateFilm_withNegativeDuration_ShouldReturn400Code() throws Exception {
        // Arrange
        var film = FilmGenerator.createFilm(0);
        var jsonFilm = GsonConverter.convertObjectToJson(film);

        film.setDuration(-1);
        var newJsonFilm = GsonConverter.convertObjectToJson(film);

        mockMvc.perform(post(urlTemplate)
                .content(jsonFilm)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(post(urlTemplate)
                        .content(newJsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

}
