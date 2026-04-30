// example: https://spring.io/guides/gs/testing-web

package com.lazarbela.ikthesis.integration;

import com.lazarbela.ikthesis.controller.SessionController;
import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.repository.SessionRepository;
import com.lazarbela.ikthesis.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Test the web layer by testing {@link SessionController}  and its dependency {@link SessionService}
 */
@WebMvcTest(SessionController.class)
@AutoConfigureRestTestClient
public class SessionControllerIntegrationTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private SessionService service;

    @MockitoBean
    private SessionRepository repository;

    private final String baseUri = "/api/v1/session";

    private final String testSessionId = "testSessionId";

    private Session testSession;

    @BeforeEach
    void setUp() {
        testSession = new Session();
        testSession.setSessionId(testSessionId);
    }

    @Test
    void getNewSessionSuccessful()
    {
        when(service.newSession()).thenReturn(testSession);

        restTestClient
                .get().uri(baseUri + "/new").exchange()
                .expectStatus().isOk()
                .expectHeader().exists("Set-Cookie")
                .expectHeader().value("Set-Cookie", cookie -> {
                    assertTrue(cookie.contains("sessionId=testSessionId"));
                    assertTrue(cookie.contains("HttpOnly"));
                    assertTrue(cookie.contains("Path=/"));
                })
                .expectBody(String.class).isEqualTo(testSessionId);
    }

    @Test
    void getExistingSessionSuccessful()
    {
        when(service.getSessionById(testSessionId)).thenReturn(testSession);

        restTestClient
                .get().uri(baseUri + "/get").header("Cookie", "sessionId=testSessionId").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.sessionId").isEqualTo(testSessionId);
    }

    @Test
    void getExistingSessionNotFound()
    {
        when(service.getSessionById("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/get").header("Cookie", "sessionId=invalidSessionId").exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void getResumePreviewSuccessful()
    {
        testSession.setResumePreviewString("# Test CV \n\n Some more text");

        when(service.getSessionById(testSessionId)).thenReturn(testSession);

        restTestClient
                .get().uri(baseUri + "/resume-preview").header("Cookie", "sessionId=testSessionId").exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("# Test CV \n\n Some more text");
    }

    @Test
    void getResumePreviewNotFound()
    {
        when(service.getSessionById("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/get").header("Cookie", "sessionId=invalidSessionId").exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void postResumePreviewSuccessful() {
        String newTestContent = "# Test CV \n\n Some more text";

        testSession.setResumePreviewString("Old content");

        when(service.getSessionById(testSessionId)).thenReturn(testSession);

        when(service.saveSession(any(Session.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        restTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/resume-preview")
                        .queryParam("content", newTestContent)
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.sessionId").isEqualTo(testSessionId);

        assertEquals(newTestContent, testSession.getResumePreviewString());
    }
}
