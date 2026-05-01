// example: https://spring.io/guides/gs/testing-web

package com.lazarbela.ikthesis.controller_slice.data_controller;

import com.lazarbela.ikthesis.controller.DataController;
import com.lazarbela.ikthesis.model.UserData;
import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.service.DataService;
import com.lazarbela.ikthesis.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Test the web layer by testing {@link DataController}  and its dependencies {@link DataService} and {@link SessionService}.
 */
@WebMvcTest(DataController.class)
@AutoConfigureRestTestClient
public class DataControllerUserDataTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private DataService dataService;

    @MockitoBean
    private SessionService sessionService;

    private final String baseUri = "/api/v1/data";

    private final String testSessionId = "testSessionId";

    private Session testSession;

    private UserData testUserData;

    @BeforeEach
    void setUp() {
        testSession = new Session();

        testUserData = new UserData(testSession);
        testUserData.setName("Minta Janos");
        testUserData.setEmailAddress("minta.janos@gmail.com");
        testUserData.setTelephoneNumber("+36301231234");

        testSession.setSessionId(testSessionId);
        testSession.setUserData(testUserData);
    }

    @Test
    void getUserDataSuccessful()
    {
        when(dataService.getUserData(testSessionId)).thenReturn(testUserData);

        restTestClient
                .get().uri(baseUri + "/user-data")
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testUserData.getId())
                .jsonPath("$.name").isEqualTo(testUserData.getName())
                .jsonPath("$.emailAddress").isEqualTo(testUserData.getEmailAddress())
                .jsonPath("$.telephoneNumber").isEqualTo(testUserData.getTelephoneNumber());

        verify(dataService, times(1)).getUserData(testSessionId);
    }

    @Test
    void getUserDataBadRequest()
    {
        when(dataService.getUserData("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/user-data")
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).getUserData("invalidSessionId");
    }

    @Test
    void postUserDataAllQueryParamsProvidedSuccessful()
    {
        UserData newUser = new UserData(testSession);
        newUser.setName("Masik Janos");
        newUser.setEmailAddress("masik.janos@gmail.com");
        newUser.setTelephoneNumber("+36703214321");

        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveUserData(any(UserData.class))).thenReturn(newUser);

        restTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/user-data")
                        .queryParam("name", "Masik Janos")
                        .queryParam("email", "masik.janos@gmail.com")
                        .queryParam("telephone", "+36703214321")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(newUser.getId())
                .jsonPath("$.name").isEqualTo("Masik Janos")
                .jsonPath("$.emailAddress").isEqualTo("masik.janos@gmail.com")
                .jsonPath("$.telephoneNumber").isEqualTo("+36703214321");

        verify(sessionService, times(1)).getSessionById(testSessionId);
        verify(dataService, times(1)).saveUserData(any(UserData.class));
    }

    @Test
    void postUserDataMissingNameProvidedSuccessful()
    {
        UserData newUser = new UserData(testSession);
        newUser.setEmailAddress("masik.janos@gmail.com");
        newUser.setTelephoneNumber("+36703214321");

        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveUserData(any(UserData.class))).thenReturn(newUser);

        restTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/user-data")
                        .queryParam("email", "masik.janos@gmail.com")
                        .queryParam("telephone", "+36703214321")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(newUser.getId())
                .jsonPath("$.emailAddress").isEqualTo("masik.janos@gmail.com")
                .jsonPath("$.telephoneNumber").isEqualTo("+36703214321");

        verify(sessionService, times(1)).getSessionById(testSessionId);
        verify(dataService, times(1)).saveUserData(any(UserData.class));
    }

    @Test
    void postUserDataMissingEmailProvidedSuccessful()
    {
        UserData newUser = new UserData(testSession);
        newUser.setName("Masik Janos");
        newUser.setTelephoneNumber("+36703214321");

        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveUserData(any(UserData.class))).thenReturn(newUser);

        restTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/user-data")
                        .queryParam("name", "Masik Janos")
                        .queryParam("telephone", "+36703214321")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(newUser.getId())
                .jsonPath("$.name").isEqualTo("Masik Janos")
                .jsonPath("$.telephoneNumber").isEqualTo("+36703214321");

        verify(sessionService, times(1)).getSessionById(testSessionId);
        verify(dataService, times(1)).saveUserData(any(UserData.class));
    }

    @Test
    void postUserDataMissingTelephoneProvidedSuccessful()
    {
        UserData newUser = new UserData(testSession);
        newUser.setName("Masik Janos");
        newUser.setEmailAddress("masik.janos@gmail.com");

        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveUserData(any(UserData.class))).thenReturn(newUser);

        restTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/user-data")
                        .queryParam("name", "Masik Janos")
                        .queryParam("email", "masik.janos@gmail.com")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(newUser.getId())
                .jsonPath("$.name").isEqualTo("Masik Janos")
                .jsonPath("$.emailAddress").isEqualTo("masik.janos@gmail.com");

        verify(sessionService, times(1)).getSessionById(testSessionId);
        verify(dataService, times(1)).saveUserData(any(UserData.class));
    }

    @Test
    void postUserDataInvalidSessionIdBadRequest()
    {
        when(dataService.getUserData("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/user-data")
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).getUserData("invalidSessionId");
    }

    @Test
    void patchUserDataAllParamsProvidedSuccessful()
    {
        UserData updatedUser = new UserData(testSession);
        updatedUser.setName("Frissitett Janos");
        updatedUser.setEmailAddress("frissitett.janos@gmail.com");
        updatedUser.setTelephoneNumber("+36303214321");

        when(dataService
                .updateUserData(
                        testSessionId,
                        Optional.of("Frissitett Janos"),
                        Optional.of("frissitett.janos@gmail.com"),
                        Optional.of( "+36303214321")))
                .thenReturn(updatedUser);

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/user-data")
                        .queryParam("name", "Frissitett Janos")
                        .queryParam("email", "frissitett.janos@gmail.com")
                        .queryParam("telephone", "+36303214321")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(updatedUser.getId())
                .jsonPath("$.name").isEqualTo("Frissitett Janos")
                .jsonPath("$.emailAddress").isEqualTo("frissitett.janos@gmail.com")
                .jsonPath("$.telephoneNumber").isEqualTo("+36303214321");

        verify(dataService, times(1)).updateUserData(
                testSessionId,
                Optional.of("Frissitett Janos"),
                Optional.of("frissitett.janos@gmail.com"),
                Optional.of( "+36303214321")
        );
    }

    @Test
    void patchUserDataMissingNameSuccessful()
    {
        UserData updatedUser = new UserData(testSession);
        updatedUser.setEmailAddress("frissitett.janos@gmail.com");
        updatedUser.setTelephoneNumber("+36303214321");

        when(dataService
                .updateUserData(
                        testSessionId,
                        Optional.empty(),
                        Optional.of("frissitett.janos@gmail.com"),
                        Optional.of( "+36303214321")))
                .thenReturn(updatedUser);

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/user-data")
                        .queryParam("email", "frissitett.janos@gmail.com")
                        .queryParam("telephone", "+36303214321")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(updatedUser.getId())
                .jsonPath("$.emailAddress").isEqualTo("frissitett.janos@gmail.com")
                .jsonPath("$.telephoneNumber").isEqualTo("+36303214321");

        verify(dataService, times(1)).updateUserData(
                testSessionId,
                Optional.empty(),
                Optional.of("frissitett.janos@gmail.com"),
                Optional.of( "+36303214321")
        );
    }

    @Test
    void patchUserDataMissingEmailSuccessful()
    {
        UserData updatedUser = new UserData(testSession);
        updatedUser.setName("Frissitett Janos");
        updatedUser.setTelephoneNumber("+36303214321");

        when(dataService
                .updateUserData(
                        testSessionId,
                        Optional.of("Frissitett Janos"),
                        Optional.empty(),
                        Optional.of( "+36303214321")))
                .thenReturn(updatedUser);

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/user-data")
                        .queryParam("name", "Frissitett Janos")
                        .queryParam("telephone", "+36303214321")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(updatedUser.getId())
                .jsonPath("$.name").isEqualTo("Frissitett Janos")
                .jsonPath("$.telephoneNumber").isEqualTo("+36303214321");

        verify(dataService, times(1)).updateUserData(
                testSessionId,
                Optional.of("Frissitett Janos"),
                Optional.empty(),
                Optional.of( "+36303214321")
        );
    }

    @Test
    void patchUserDataMissingTelephoneSuccessful()
    {
        UserData updatedUser = new UserData(testSession);
        updatedUser.setName("Frissitett Janos");
        updatedUser.setEmailAddress("frissitett.janos@gmail.com");

        when(dataService
                .updateUserData(
                        testSessionId,
                        Optional.of("Frissitett Janos"),
                        Optional.of("frissitett.janos@gmail.com"),
                        Optional.empty()))
                .thenReturn(updatedUser);

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/user-data")
                        .queryParam("name", "Frissitett Janos")
                        .queryParam("email", "frissitett.janos@gmail.com")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(updatedUser.getId())
                .jsonPath("$.name").isEqualTo("Frissitett Janos")
                .jsonPath("$.emailAddress").isEqualTo("frissitett.janos@gmail.com");

        verify(dataService, times(1)).updateUserData(
                testSessionId,
                Optional.of("Frissitett Janos"),
                Optional.of("frissitett.janos@gmail.com"),
                Optional.empty()
        );
    }
}
