// example: https://spring.io/guides/gs/testing-web

package com.lazarbela.ikthesis.controller_slice.data_controller;

import com.lazarbela.ikthesis.controller.DataController;
import com.lazarbela.ikthesis.model.WorkExperience;
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

import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.mockito.Mockito.*;

/**
 * Test the web layer by testing {@link DataController}  and its dependencies {@link DataService} and {@link SessionService}.
 */
@WebMvcTest(DataController.class)
@AutoConfigureRestTestClient
public class DataControllerWorkExperiencesTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private DataService dataService;

    @MockitoBean
    private SessionService sessionService;

    private final String baseUri = "/api/v1/data";

    private final String testSessionId = "testSessionId";

    private final Session testSession = new Session();

    private final HashSet<WorkExperience> testWorkExperiences = new LinkedHashSet<>();

    private final WorkExperience testWorkExperience1 = new WorkExperience(testSession, "Test content 1.");

    private final WorkExperience testWorkExperience2 = new WorkExperience(testSession, "Test content 2.");

    @BeforeEach
    void setUp() {
        testSession.setSessionId(testSessionId);
        testSession.setWorkExperiences(testWorkExperiences);

        testWorkExperiences.add(testWorkExperience1);
        testWorkExperiences.add(testWorkExperience2);
    }

    @Test
    void getWorkExperiencesSuccessful()
    {
        when(dataService.getWorkExperiences(testSessionId)).thenReturn(testWorkExperiences);

        restTestClient
                .get().uri(baseUri + "/work-experiences")
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(testWorkExperience1.getId())
                .jsonPath("$[0].content").isEqualTo(testWorkExperience1.getContent())
                .jsonPath("$[1].id").isEqualTo(testWorkExperience2.getId())
                .jsonPath("$[1].content").isEqualTo(testWorkExperience2.getContent());

        verify(dataService, times(1)).getWorkExperiences(testSessionId);
    }

    @Test
    void getWorkExperiencesBadRequest()
    {
        when(dataService.getWorkExperiences("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/work-experiences")
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).getWorkExperiences("invalidSessionId");
    }

    @Test
    void postWorkExperienceSuccessful()
    {
        WorkExperience testWorkExperience = new WorkExperience(testSession, "Test WorkExperience content.");

        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveWorkExperience(testWorkExperience)).thenReturn(testWorkExperience);

        restTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/work-experiences")
                        .queryParam("content", "Test WorkExperience content.")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testWorkExperience.getId())
                .jsonPath("$.content").isEqualTo(testWorkExperience.getContent());

        verify(sessionService, times(1)).getSessionById(testSessionId);
        verify(dataService, times(1)).saveWorkExperience(testWorkExperience);
    }

    @Test
    void postWorkExperienceMissingQueryParamBadRequest()
    {
        WorkExperience testWorkExperience = new WorkExperience(testSession, "Test WorkExperience content.");

        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveWorkExperience(testWorkExperience)).thenReturn(testWorkExperience);

        restTestClient
                .post().uri(baseUri + "/work-experiences")
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void postWorkExperienceInvalidSessionIdBadRequest()
    {
        when(dataService.getWorkExperiences("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/work-experiences")
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).getWorkExperiences("invalidSessionId");
    }

    @Test
    void pathWorkExperienceSuccessful()
    {
        WorkExperience testWorkExperience = new WorkExperience(testSession, "Test WorkExperience content.");
        testWorkExperience.setContent("Updated content 1.");

        when(dataService.updateWorkExperience(testSessionId, testWorkExperience.getId(), "Updated content 1."))
                .thenReturn(testWorkExperience);

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/work-experiences")
                        .queryParam("id", testWorkExperience.getId())
                        .queryParam("content", "Updated content 1.")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testWorkExperience.getId())
                .jsonPath("$.content").isEqualTo("Updated content 1.");

        verify(dataService, times(1))
                .updateWorkExperience(testSessionId, testWorkExperience.getId(),"Updated content 1.");
    }

    @Test
    void pathWorkExperienceMissingIdParamBadRequest()
    {
        WorkExperience testWorkExperience = new WorkExperience(testSession, "Test WorkExperience content.");
        testWorkExperience.setContent("Updated content 1.");

        when(dataService.updateWorkExperience("invalidSessionId", testWorkExperience.getId(), "Updated content 1."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                    .path(baseUri + "/work-experiences")
                    .queryParam("content", "Updated content 1.")
                    .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void pathWorkExperienceMissingContentParamBadRequest()
    {
        WorkExperience testWorkExperience = new WorkExperience(testSession, "Test WorkExperience content.");
        testWorkExperience.setContent("Updated content 1.");

        when(dataService.updateWorkExperience("invalidSessionId", testWorkExperience.getId(), "Updated content 1."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/work-experiences")
                        .queryParam("id", testWorkExperience.getId())
                        .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void pathWorkExperienceInvalidSessionIdBadRequest()
    {
        WorkExperience testWorkExperience = new WorkExperience(testSession, "Test WorkExperience content.");
        testWorkExperience.setContent("Updated content 1.");

        when(dataService.updateWorkExperience("invalidSessionId", testWorkExperience.getId(), "Updated content 1."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/work-experiences")
                        .queryParam("id", testWorkExperience.getId())
                        .queryParam("content", "Updated content 1.")
                        .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1))
                .updateWorkExperience("invalidSessionId", testWorkExperience.getId(), "Updated content 1.");
    }

    @Test
    void deleteWorkExperiencesSuccessful()
    {
        WorkExperience testWorkExperience = new WorkExperience(testSession, "Test WorkExperience content.");

        when(dataService.deleteWorkExperience(testSessionId, testWorkExperience.getId())).thenReturn(testWorkExperience);

        restTestClient
                .delete().uri(uriBuilder -> uriBuilder
                    .path(baseUri + "/work-experiences")
                    .queryParam("id", testWorkExperience.getId())
                    .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isNoContent();

        verify(dataService, times(1)).deleteWorkExperience(testSessionId, testWorkExperience.getId());
    }

    @Test
    void deleteWorkExperienceInvalidWorkExperienceIdBadRequest()
    {
        WorkExperience testWorkExperience = new WorkExperience(testSession, "Test WorkExperience content.");

        when(dataService.deleteWorkExperience(testSessionId, testWorkExperience.getId()))
                .thenThrow(new IllegalArgumentException("WorkExperience id not found"));

        restTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/work-experiences")
                        .queryParam("id", testWorkExperience.getId())
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).deleteWorkExperience(testSessionId, testWorkExperience.getId());
    }
}
