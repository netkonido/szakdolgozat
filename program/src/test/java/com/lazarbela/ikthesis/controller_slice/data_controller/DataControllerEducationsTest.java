// example: https://spring.io/guides/gs/testing-web

package com.lazarbela.ikthesis.controller_slice.data_controller;

import com.lazarbela.ikthesis.controller.DataController;
import com.lazarbela.ikthesis.model.Education;
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
public class DataControllerEducationsTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private DataService dataService;

    @MockitoBean
    private SessionService sessionService;

    private final String baseUri = "/api/v1/data";

    private final String testSessionId = "testSessionId";

    private final Session testSession = new Session();

    private final HashSet<Education> testEducations = new LinkedHashSet<>();

    private final Education testEducation1 = new Education(testSession, "Test content 1.");

    private final Education testEducation2 = new Education(testSession, "Test content 2.");

    @BeforeEach
    void setUp() {
        testSession.setSessionId(testSessionId);
        testSession.setEducations(testEducations);

        testEducations.add(testEducation1);
        testEducations.add(testEducation2);
    }

    @Test
    void getEducationsSuccessful()
    {
        when(dataService.getEducations(testSessionId)).thenReturn(testEducations);

        restTestClient
                .get().uri(baseUri + "/educations")
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(testEducation1.getId())
                .jsonPath("$[0].content").isEqualTo(testEducation1.getContent())
                .jsonPath("$[1].id").isEqualTo(testEducation2.getId())
                .jsonPath("$[1].content").isEqualTo(testEducation2.getContent());

        verify(dataService, times(1)).getEducations(testSessionId);
    }

    @Test
    void getEducationsBadRequest()
    {
        when(dataService.getEducations("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/educations")
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).getEducations("invalidSessionId");
    }

    @Test
    void postEducationSuccessful()
    {
        Education testEducation = new Education(testSession, "Test Education content.");

        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveEducation(testEducation)).thenReturn(testEducation);

        restTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/educations")
                        .queryParam("content", "Test Education content.")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testEducation.getId())
                .jsonPath("$.content").isEqualTo(testEducation.getContent());

        verify(sessionService, times(1)).getSessionById(testSessionId);
        verify(dataService, times(1)).saveEducation(testEducation);
    }

    @Test
    void postEducationMissingQueryParamBadRequest()
    {
        Education testEducation = new Education(testSession, "Test Education content.");

        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveEducation(testEducation)).thenReturn(testEducation);

        restTestClient
                .post().uri(baseUri + "/educations")
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void postEducationInvalidSessionIdBadRequest()
    {
        when(dataService.getEducations("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/educations")
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).getEducations("invalidSessionId");
    }

    @Test
    void pathEducationSuccessful()
    {
        Education testEducation = new Education(testSession, "Test Education content.");
        testEducation.setContent("Updated content 1.");

        when(dataService.updateEducation(testSessionId, testEducation.getId(), "Updated content 1."))
                .thenReturn(testEducation);

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/educations")
                        .queryParam("id", testEducation.getId())
                        .queryParam("content", "Updated content 1.")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testEducation.getId())
                .jsonPath("$.content").isEqualTo("Updated content 1.");

        verify(dataService, times(1))
                .updateEducation(testSessionId, testEducation.getId(),"Updated content 1.");
    }

    @Test
    void pathEducationMissingIdParamBadRequest()
    {
        Education testEducation = new Education(testSession, "Test Education content.");
        testEducation.setContent("Updated content 1.");

        when(dataService.updateEducation("invalidSessionId", testEducation.getId(), "Updated content 1."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                    .path(baseUri + "/educations")
                    .queryParam("content", "Updated content 1.")
                    .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void pathEducationMissingContentParamBadRequest()
    {
        Education testEducation = new Education(testSession, "Test Education content.");
        testEducation.setContent("Updated content 1.");

        when(dataService.updateEducation("invalidSessionId", testEducation.getId(), "Updated content 1."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/educations")
                        .queryParam("id", testEducation.getId())
                        .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void pathEducationInvalidSessionIdBadRequest()
    {
        Education testEducation = new Education(testSession, "Test Education content.");
        testEducation.setContent("Updated content 1.");

        when(dataService.updateEducation("invalidSessionId", testEducation.getId(), "Updated content 1."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/educations")
                        .queryParam("id", testEducation.getId())
                        .queryParam("content", "Updated content 1.")
                        .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1))
                .updateEducation("invalidSessionId", testEducation.getId(), "Updated content 1.");
    }

    @Test
    void deleteEducationsSuccessful()
    {
        Education testEducation = new Education(testSession, "Test Education content.");

        when(dataService.deleteEducation(testSessionId, testEducation.getId())).thenReturn(testEducation);

        restTestClient
                .delete().uri(uriBuilder -> uriBuilder
                    .path(baseUri + "/educations")
                    .queryParam("id", testEducation.getId())
                    .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isNoContent();

        verify(dataService, times(1)).deleteEducation(testSessionId, testEducation.getId());
    }

    @Test
    void deleteEducationInvalidEducationIdBadRequest()
    {
        Education testEducation = new Education(testSession, "Test Education content.");

        when(dataService.deleteEducation(testSessionId, testEducation.getId()))
                .thenThrow(new IllegalArgumentException("Education id not found"));

        restTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/educations")
                        .queryParam("id", testEducation.getId())
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).deleteEducation(testSessionId, testEducation.getId());
    }
}
