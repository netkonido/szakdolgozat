// example: https://spring.io/guides/gs/testing-web

package com.lazarbela.ikthesis.integration.data_controller;

import com.lazarbela.ikthesis.controller.DataController;
import com.lazarbela.ikthesis.model.JobDescription;
import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.repository.SessionRepository;
import com.lazarbela.ikthesis.service.SessionService;
import com.lazarbela.ikthesis.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.mockito.Mockito.*;

/**
 * Test the web layer by testing {@link DataController}  and its dependencies {@link DataService} and {@link SessionService}
 */
@WebMvcTest(DataController.class)
@AutoConfigureRestTestClient
public class DataControllerJobDescriptionTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private DataService dataService;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private SessionRepository repository;

    private final String baseUri = "/api/v1/data";

    private final String testSessionId = "testSessionId";

    private Session testSession;

    private JobDescription testJobDescription;

    @BeforeEach
    void setUp() {
        testSession = new Session();

        testJobDescription = new JobDescription(testSession, "Test job description content.");

        testSession.setSessionId(testSessionId);
        testSession.setJobDescription(testJobDescription);
    }

    @Test
    void getJobDescriptionSuccessful()
    {
        when(dataService.getJobDescription(testSessionId)).thenReturn(testJobDescription);

        restTestClient
                .get().uri(baseUri + "/job-description")
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testJobDescription.getId())
                .jsonPath("$.content").isEqualTo(testJobDescription.getContent());

        verify(dataService, times(1)).getJobDescription(testSessionId);
    }

    @Test
    void getJobDescriptionBadRequest()
    {
        when(dataService.getJobDescription("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/job-description")
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).getJobDescription("invalidSessionId");
    }

    @Test
    void postJobDescriptionSuccessful()
    {
        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveJobDescription(testJobDescription)).thenReturn(testJobDescription);

        restTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/job-description")
                        .queryParam("content", "Test job description content.")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testJobDescription.getId())
                .jsonPath("$.content").isEqualTo(testJobDescription.getContent());

        verify(sessionService, times(1)).getSessionById(testSessionId);
        verify(dataService, times(1)).saveJobDescription(testJobDescription);
    }

    @Test
    void postJobDescriptionMissingQueryParamBadRequest()
    {
        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveJobDescription(testJobDescription)).thenReturn(testJobDescription);

        restTestClient
                .post().uri(baseUri + "/job-description")
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isBadRequest();

        verify(sessionService, times(1)).getSessionById(testSessionId);
        verify(dataService, times(1)).saveJobDescription(testJobDescription);
    }

    @Test
    void postJobDescriptionInvalidSessionIdBadRequest()
    {
        when(dataService.getJobDescription("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/job-description")
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).getJobDescription("invalidSessionId");
    }

    @Test
    void patchJobDescriptionSuccessful()
    {
        testJobDescription.setContent("Updated job description content.");

        when(dataService.updateJobDescription(testSessionId, "Updated job description content."))
                .thenReturn(testJobDescription);

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/job-description")
                        .queryParam("content", "Updated job description content.")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testJobDescription.getId())
                .jsonPath("$.content").isEqualTo("Updated job description content.");

        verify(dataService, times(1))
                .updateJobDescription(testSessionId, "Updated job description content.");
    }

    @Test
    void patchJobDescriptionInvalidSessionIdBadRequest()
    {
        testJobDescription.setContent("Updated job description content.");

        when(dataService.updateJobDescription("invalidSessionId", "Updated job description content."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                    .path(baseUri + "/job-description")
                    .queryParam("content", "Updated job description content.")
                    .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1))
                .updateJobDescription("invalidSessionId", "Updated job description content.");
    }
}
