// example: https://spring.io/guides/gs/testing-web

package com.lazarbela.ikthesis.integration;

import com.lazarbela.ikthesis.controller.SessionController;
import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.repository.SessionRepository;
import com.lazarbela.ikthesis.service.SessionService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test the web layer by testing {@link SessionController}  and its dependency {@link SessionService}
 */
@WebMvcTest(SessionController.class)
@AutoConfigureRestTestClient
public class SessionControllerIntegrationTest {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService service;

    @MockitoBean
    private SessionRepository repository;

    private String baseUri = "/api/v1/session";

    @Test
    void getNewSessionSuccessful()
    {
        Session testSession = new Session();
        testSession.setSessionId("testSessionId");

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
                .expectBody(String.class).isEqualTo("testSessionId");
    }

    @Test
    void getExistingSessionSuccessful()
    {
        Session testSession = new Session();
        testSession.setSessionId("testSessionId");

        when(service.getSessionById("testSessionId")).thenReturn(testSession);

        restTestClient
                .get().uri(baseUri + "/get").cookie("sessionId", "testSessionId").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.sessionId").isEqualTo("testSessionId");
    }

    @Test
    void getExistingSessionNotFound()
    {
        when(service.getSessionById("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/get").cookie("sessionId", "invalidSessionId").exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void getResumePreviewSuccessful() {
        Session testSession = new Session();
        testSession.setSessionId("testSessionId");
        testSession.setResumePreviewString("# Test CV \n\n Some more text");

        when(service.getSessionById("testSessionId")).thenReturn(testSession);

        restTestClient
                .get().uri(baseUri + "/resume-preview").cookie("sessionId", "testSessionId").exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("# Test CV \n\n Some more text");
    }

    @Test
    void getResumePreviewNotFound()
    {
        when(service.getSessionById("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/get").cookie("sessionId", "invalidSessionId").exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}
