package piron.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.piron.UserApplication;
import org.piron.common.dto.RoleDTO;
import org.piron.common.dto.request.CreateUserRequest;
import org.piron.common.dto.response.UserResponseDTO;
import org.piron.security.config.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import piron.initializer.PostgresInitializer;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.piron.common.constants.ApiConstants.API_USERS_PATH;
import static org.piron.common.constants.ExceptionConstants.ERROR_DELETING_ACCOUNT;
import static org.piron.common.constants.ExceptionConstants.ERROR_ROLE_NOT_FOUND;
import static org.piron.common.constants.ExceptionConstants.ERROR_USER_NOT_FOUND;
import static org.piron.common.constants.ExceptionConstants.ERROR_USER_NOT_UNIQUE;
import static org.springframework.http.MediaType.APPLICATION_JSON;


@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@DBRider
@DBUnit(caseSensitiveTableNames = true, schema = "public")
@ContextConfiguration(initializers = PostgresInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = UserApplication.class)
class UserControllerTest {

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtService jwtService;

    private static final String USER_LOGIN = "test";

    private static final String ADMIN_LOGIN = "admin";

    private static final String ADMIN_PASSWORD = "admin";

    private static final String TEST_LOGIN = "testLogin";

    private static final String TEST_PASSWORD = "testPassword";

    private static final String ROLE_USER = "ROLE_USER";

    public static final String INVALID_ROLE = "ROLE_US";

    @Test
    @DisplayName("Тест на ошибку авторизации, HttpStatus 403")
    void shouldReturn403() {

        CreateUserRequest request = CreateUserRequest.builder()
                .login(TEST_LOGIN)
                .password(TEST_PASSWORD)
                .roles(Set.of(new RoleDTO(ROLE_USER)))
                .build();

        webTestClient.post()
                .uri(API_USERS_PATH)
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DataSet(value = "CorrectDBFiller.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 400 если создать юзера, который уже существует")
    void shouldReturn400_WhenTryingCreateUserWhoAlreadyExists() {

        CreateUserRequest request = CreateUserRequest.builder()
                .login(ADMIN_LOGIN)
                .password(ADMIN_PASSWORD)
                .roles(Set.of(new RoleDTO(ROLE_USER)))
                .build();

        UserResponseDTO responseBody = webTestClient.post()
                .uri(API_USERS_PATH)
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, jwtService.generateToken(ADMIN_LOGIN))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(UserResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertTrue(responseBody.isHasError());
        assertNull(responseBody.getLogin());
        assertEquals(String.format(ERROR_USER_NOT_UNIQUE, request.getLogin()), responseBody.getMessage());
    }

    @Test
    @DataSet(value = "CorrectDBFiller.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 201 если создать пользователя с валидными данными")
    void shouldReturn201_WhenCreatingValidUser() {

        CreateUserRequest request = CreateUserRequest.builder()
                .login(TEST_LOGIN)
                .password(TEST_PASSWORD)
                .roles(Set.of(new RoleDTO(ROLE_USER)))
                .build();

        UserResponseDTO responseBody = webTestClient.post()
                .uri(API_USERS_PATH)
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, jwtService.generateToken(ADMIN_LOGIN))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertFalse(responseBody.isHasError());
        assertEquals(TEST_LOGIN, responseBody.getLogin());
        assertNull(responseBody.getMessage());
    }

    @Test
    @DataSet(value = "CorrectDBFiller.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 400 если создать юзера, с ролью, который не существует")
    void shouldReturn400_WhenTryingCreateUserWithInvalidRole() {

        CreateUserRequest request = CreateUserRequest.builder()
                .login(TEST_LOGIN)
                .password(TEST_PASSWORD)
                .roles(Set.of(new RoleDTO(INVALID_ROLE)))
                .build();

        UserResponseDTO responseBody = webTestClient.post()
                .uri(API_USERS_PATH)
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, jwtService.generateToken(ADMIN_LOGIN))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(UserResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertTrue(responseBody.isHasError());
        assertNull(responseBody.getLogin());
        assertEquals(String.format(ERROR_ROLE_NOT_FOUND, INVALID_ROLE), responseBody.getMessage());
    }

    @Test
    @DataSet(value = "CorrectDBFiller.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 400 если удалить свой аккаунт")
    void shouldReturn400_WhenTryingDeleteOwnAccount() {
        UserResponseDTO responseBody = webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path(API_USERS_PATH + "/{login}")
                        .build(ADMIN_LOGIN))
                .accept(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtService.generateToken(ADMIN_LOGIN))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(UserResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertTrue(responseBody.isHasError());
        assertNull(responseBody.getLogin());
        assertEquals(ERROR_DELETING_ACCOUNT, responseBody.getMessage());
    }

    @Test
    @DataSet(value = "CorrectDBFiller.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 404 если удалить аккаунт, который не существует")
    void shouldReturn404_WhenTryingDeleteAccountWhichDoesNotExist() {
        UserResponseDTO responseBody = webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path(API_USERS_PATH + "/{login}")
                        .build(TEST_LOGIN))
                .accept(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtService.generateToken(ADMIN_LOGIN))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(UserResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertTrue(responseBody.isHasError());
        assertNull(responseBody.getLogin());
        assertEquals(String.format(ERROR_USER_NOT_FOUND, TEST_LOGIN), responseBody.getMessage());
    }

    @Test
    @DataSet(value = "CorrectDBFiller.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 200 при удалении аккаунта")
    void shouldReturn200_WhenDeletingAccount() {
        UserResponseDTO responseBody = webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path(API_USERS_PATH + "/{login}")
                        .build(USER_LOGIN))
                .accept(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtService.generateToken(ADMIN_LOGIN))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertFalse(responseBody.isHasError());
        assertEquals(USER_LOGIN, responseBody.getLogin());
        assertNull(responseBody.getMessage());
    }

    @Test
    @DataSet(value = "CorrectDBFiller.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 200 при обновлении существующего аккаунта")
    void shouldReturn200_WhenUpdatingAccount() {

        CreateUserRequest request = CreateUserRequest.builder()
                .login(USER_LOGIN)
                .password(TEST_PASSWORD)
                .build();

        UserResponseDTO responseBody = webTestClient.put()
                .uri(API_USERS_PATH)
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, jwtService.generateToken(ADMIN_LOGIN))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertFalse(responseBody.isHasError());
        assertEquals(USER_LOGIN, responseBody.getLogin());
        assertNull(responseBody.getMessage());
    }

    @Test
    @DataSet(value = "CorrectDBFiller.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 404 при обновлении аккаунта, который не существует")
    void shouldReturn404_WhenUpdatingAccountWhichDoesNotExist() {

        CreateUserRequest request = CreateUserRequest.builder()
                .login(TEST_LOGIN)
                .password(TEST_PASSWORD)
                .build();

        UserResponseDTO responseBody = webTestClient.put()
                .uri(API_USERS_PATH)
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, jwtService.generateToken(ADMIN_LOGIN))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(UserResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertTrue(responseBody.isHasError());
        assertNull(responseBody.getLogin());
        assertEquals(String.format(ERROR_USER_NOT_FOUND, request.getLogin()), responseBody.getMessage());
    }

    @Test
    @DataSet(value = "CorrectDBFiller.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 400 при обновлении аккаунта, не указав логин")
    void shouldReturn400_WhenUpdatingAccountWithoutLogin() {

        CreateUserRequest request = CreateUserRequest.builder()
                .password(TEST_PASSWORD)
                .build();

        UserResponseDTO responseBody = webTestClient.put()
                .uri(API_USERS_PATH)
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, jwtService.generateToken(ADMIN_LOGIN))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(UserResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertTrue(responseBody.isHasError());
        assertNull(responseBody.getLogin());
        assertNotNull(responseBody.getMessage());
    }

    static Stream<Arguments> shouldReturn400_WhenCreatingAccountWithInvalidFields() {
        return Stream.of(
                Arguments.of(null, TEST_PASSWORD, Set.of(new RoleDTO(ROLE_USER))),
                Arguments.of(TEST_LOGIN, null, Set.of(new RoleDTO(ROLE_USER))),
                Arguments.of(TEST_LOGIN, TEST_PASSWORD, null),
                Arguments.of(TEST_LOGIN, TEST_PASSWORD, Set.of())
        );
    }

    @ParameterizedTest(name = "Login: {0}, Password: {1}, Roles: {2}")
    @DataSet(value = "CorrectDBFiller.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 400 при создании аккаунта, с невалидными данными")
    @MethodSource
    void shouldReturn400_WhenCreatingAccountWithInvalidFields(String login, String password, Set<RoleDTO> roles) {

        CreateUserRequest request = CreateUserRequest.builder()
                .login(login)
                .password(password)
                .roles(roles)
                .build();

        UserResponseDTO responseBody = webTestClient.post()
                .uri(API_USERS_PATH)
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, jwtService.generateToken(ADMIN_LOGIN))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(UserResponseDTO.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertTrue(responseBody.isHasError());
        assertNull(responseBody.getLogin());
        assertNotNull(responseBody.getMessage());
    }
}
