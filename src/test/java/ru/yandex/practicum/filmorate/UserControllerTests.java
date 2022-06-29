package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.testUtils.GsonConverter;
import ru.yandex.practicum.filmorate.testUtils.UserGenerator;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;
    private final String urlTemplate = "/users";

    @Test
    public void GetAllUsers_executeRequest_ShouldReturn200Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        var jsonUser = GsonConverter.convertObjectToJson(user);

        // Act
        mockMvc.perform(post(urlTemplate)
                .content(jsonUser)
                .contentType(MediaType.APPLICATION_JSON));

        var result = mockMvc.perform(get(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();

        // Assert
        var actualResponse = result.getResponse().getContentAsString();
        var actualStatusCode = result.getResponse().getStatus();

        user.setId(1);
        Assertions.assertEquals("[" + GsonConverter.convertObjectToJson(user) + "]", actualResponse);
        Assertions.assertEquals(200, actualStatusCode);
    }

    @Test
    public void CreateUser_withAllParams_ShouldReturn200Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        var jsonUser = GsonConverter.convertObjectToJson(user);

        // Act
        var result = mockMvc
                .perform(post(urlTemplate)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualResponse = result.getResponse().getContentAsString();
        var actualStatusCode = result.getResponse().getStatus();

        user.setId(1);
        Assertions.assertEquals(GsonConverter.convertObjectToJson(user), actualResponse);
        Assertions.assertEquals(200, actualStatusCode);
    }

    @Test
    public void CreateUser_withAlreadyExistingEmail_ShouldReturn400Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        var jsonUser = GsonConverter.convertObjectToJson(user);

        user.setId(2);
        var newJsonUser = GsonConverter.convertObjectToJson(user);

        mockMvc.perform(post(urlTemplate)
                .content(jsonUser)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(post(urlTemplate)
                        .content(newJsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void CreateUser_withInvalidEmail_ShouldReturn400Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        user.setEmail("test");
        var jsonUser = GsonConverter.convertObjectToJson(user);

        // Act
        var result = mockMvc
                .perform(post(urlTemplate)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();
        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void CreateUser_withEmptyEmail_ShouldReturn400Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        user.setEmail(null);
        var jsonUser = GsonConverter.convertObjectToJson(user);

        // Act
        var result = mockMvc
                .perform(post(urlTemplate)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();
        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void CreateUser_withEmptyLogin_ShouldReturn400Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        user.setLogin("");
        var jsonUser = GsonConverter.convertObjectToJson(user);

        // Act
        var result = mockMvc
                .perform(post(urlTemplate)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();
        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void CreateUser_withNullName_ShouldReturnUserWithLoginEqualsName() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        user.setName("");
        var jsonUser = GsonConverter.convertObjectToJson(user);

        // Act
        var result = mockMvc
                .perform(post(urlTemplate)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualResponse = result.getResponse().getContentAsString();
        var actualStatusCode = result.getResponse().getStatus();

        user.setId(1);
        user.setName(user.getLogin());
        Assertions.assertEquals(GsonConverter.convertObjectToJson(user), actualResponse);
        Assertions.assertEquals(200, actualStatusCode);
    }

    @Test
    public void CreateUser_withBirthDayInPresent_ShouldReturn400Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        user.setBirthday(LocalDate.now());
        var jsonUser = GsonConverter.convertObjectToJson(user);

        // Act
        var result = mockMvc
                .perform(post(urlTemplate)
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void UpdateUser_withAllParams_ShouldReturn200Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        var jsonUser = GsonConverter.convertObjectToJson(user);

        var newUser = UserGenerator.createUser(1);
        newUser.setName("NemName");
        newUser.setLogin("NemName");
        newUser.setEmail("newEmail@test.ru");
        newUser.setBirthday(LocalDate.of(1990, 3, 3));
        var newJsonUser = GsonConverter.convertObjectToJson(newUser);

        mockMvc.perform(post(urlTemplate)
                .content(jsonUser)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(put(urlTemplate)
                        .content(newJsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualResponse = result.getResponse().getContentAsString();
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(GsonConverter.convertObjectToJson(newUser), actualResponse);
        Assertions.assertEquals(200, actualStatusCode);
    }

    @Test
    public void UpdateUser_withInvalidEmail_ShouldReturn400Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        var jsonUser = GsonConverter.convertObjectToJson(user);

        user.setId(1);
        user.setEmail("test");
        var newJsonUser = GsonConverter.convertObjectToJson(user);

        mockMvc.perform(post(urlTemplate)
                .content(jsonUser)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(put(urlTemplate)
                        .content(newJsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void UpdateUser_withEmptyEmail_ShouldReturn400Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        var jsonUser = GsonConverter.convertObjectToJson(user);

        user.setId(1);
        user.setEmail("");
        var newJsonUser = GsonConverter.convertObjectToJson(user);

        mockMvc.perform(post(urlTemplate)
                .content(jsonUser)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(put(urlTemplate)
                        .content(newJsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void UpdateUser_withEmptyLogin_ShouldReturn400Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        var jsonUser = GsonConverter.convertObjectToJson(user);

        user.setId(1);
        user.setLogin("");
        var newJsonUser = GsonConverter.convertObjectToJson(user);

        mockMvc.perform(post(urlTemplate)
                .content(jsonUser)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(put(urlTemplate)
                        .content(newJsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }

    @Test
    public void UpdateUser_withEmptyName_ShouldReturn400Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        var jsonUser = GsonConverter.convertObjectToJson(user);

        user.setId(1);
        user.setName("");
        var userToUpdate = GsonConverter.convertObjectToJson(user);

        user.setName(user.getLogin());
        var expectedResponse = GsonConverter.convertObjectToJson(user);

        mockMvc.perform(post(urlTemplate)
                .content(jsonUser)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(put(urlTemplate)
                        .content(userToUpdate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();
        var actualResponse = result.getResponse().getContentAsString();

        Assertions.assertEquals(200, actualStatusCode);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void UpdateUser_withBirthdayInPresent_ShouldReturn400Code() throws Exception {
        // Arrange
        var user = UserGenerator.createUser(0);
        var jsonUser = GsonConverter.convertObjectToJson(user);

        user.setId(1);
        user.setBirthday(LocalDate.now().plusDays(1));
        var newJsonUser = GsonConverter.convertObjectToJson(user);

        mockMvc.perform(post(urlTemplate)
                .content(jsonUser)
                .contentType(MediaType.APPLICATION_JSON));

        // Act
        var result = mockMvc.perform(put(urlTemplate)
                        .content(newJsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        var actualStatusCode = result.getResponse().getStatus();

        Assertions.assertEquals(400, actualStatusCode);
    }
}
