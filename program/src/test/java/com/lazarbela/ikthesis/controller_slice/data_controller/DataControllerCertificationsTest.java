// example: https://spring.io/guides/gs/testing-web

package com.lazarbela.ikthesis.controller_slice.data_controller;

import com.lazarbela.ikthesis.controller.DataController;
import com.lazarbela.ikthesis.model.Certification;
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
public class DataControllerCertificationsTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private DataService dataService;

    @MockitoBean
    private SessionService sessionService;

    private final String baseUri = "/api/v1/data";

    private final String testSessionId = "testSessionId";

    private final Session testSession = new Session();

    private final HashSet<Certification> testCertifications = new LinkedHashSet<>();

    private final Certification testCertification1 = new Certification(testSession, "Test content 1.");

    private final Certification testCertification2 = new Certification(testSession, "Test content 2.");

    @BeforeEach
    void setUp() {
        testSession.setSessionId(testSessionId);
        testSession.setCertifications(testCertifications);

        testCertifications.add(testCertification1);
        testCertifications.add(testCertification2);
    }

    @Test
    void getCertificationsSuccessful()
    {
        when(dataService.getCertifications(testSessionId)).thenReturn(testCertifications);

        restTestClient
                .get().uri(baseUri + "/certifications")
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(testCertification1.getId())
                .jsonPath("$[0].content").isEqualTo(testCertification1.getContent())
                .jsonPath("$[1].id").isEqualTo(testCertification2.getId())
                .jsonPath("$[1].content").isEqualTo(testCertification2.getContent());

        verify(dataService, times(1)).getCertifications(testSessionId);
    }

    @Test
    void getCertificationsBadRequest()
    {
        when(dataService.getCertifications("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/certifications")
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).getCertifications("invalidSessionId");
    }

    @Test
    void postCertificationSuccessful()
    {
        Certification testCertification = new Certification(testSession, "Test certification content.");

        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveCertification(testCertification)).thenReturn(testCertification);

        restTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/certifications")
                        .queryParam("content", "Test certification content.")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testCertification.getId())
                .jsonPath("$.content").isEqualTo(testCertification.getContent());

        verify(sessionService, times(1)).getSessionById(testSessionId);
        verify(dataService, times(1)).saveCertification(testCertification);
    }

    @Test
    void postCertificationMissingQueryParamBadRequest()
    {
        Certification testCertification = new Certification(testSession, "Test certification content.");

        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveCertification(testCertification)).thenReturn(testCertification);

        restTestClient
                .post().uri(baseUri + "/certifications")
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void postCertificationInvalidSessionIdBadRequest()
    {
        when(dataService.getCertifications("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/certifications")
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).getCertifications("invalidSessionId");
    }

    @Test
    void pathCertificationSuccessful()
    {
        Certification testCertification = new Certification(testSession, "Test certification content.");
        testCertification.setContent("Updated content 1.");

        when(dataService.updateCertification(testSessionId, testCertification.getId(), "Updated content 1."))
                .thenReturn(testCertification);

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/certifications")
                        .queryParam("id", testCertification.getId())
                        .queryParam("content", "Updated content 1.")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testCertification.getId())
                .jsonPath("$.content").isEqualTo("Updated content 1.");

        verify(dataService, times(1))
                .updateCertification(testSessionId, testCertification.getId(),"Updated content 1.");
    }

    @Test
    void pathCertificationMissingIdParamBadRequest()
    {
        Certification testCertification = new Certification(testSession, "Test certification content.");
        testCertification.setContent("Updated content 1.");

        when(dataService.updateCertification("invalidSessionId", testCertification.getId(), "Updated content 1."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                    .path(baseUri + "/certifications")
                    .queryParam("content", "Updated content 1.")
                    .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void pathCertificationMissingContentParamBadRequest()
    {
        Certification testCertification = new Certification(testSession, "Test certification content.");
        testCertification.setContent("Updated content 1.");

        when(dataService.updateCertification("invalidSessionId", testCertification.getId(), "Updated content 1."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/certifications")
                        .queryParam("id", testCertification.getId())
                        .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void pathCertificationInvalidSessionIdBadRequest()
    {
        Certification testCertification = new Certification(testSession, "Test certification content.");
        testCertification.setContent("Updated content 1.");

        when(dataService.updateCertification("invalidSessionId", testCertification.getId(), "Updated content 1."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/certifications")
                        .queryParam("id", testCertification.getId())
                        .queryParam("content", "Updated content 1.")
                        .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1))
                .updateCertification("invalidSessionId", testCertification.getId(), "Updated content 1.");
    }

    @Test
    void deleteCertificationsSuccessful()
    {
        Certification testCertification = new Certification(testSession, "Test certification content.");

        when(dataService.deleteCertification(testSessionId, testCertification.getId())).thenReturn(testCertification);

        restTestClient
                .delete().uri(uriBuilder -> uriBuilder
                    .path(baseUri + "/certifications")
                    .queryParam("id", testCertification.getId())
                    .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isNoContent();

        verify(dataService, times(1)).deleteCertification(testSessionId, testCertification.getId());
    }

    @Test
    void deleteCertificationInvalidCertificationIdBadRequest()
    {
        Certification testCertification = new Certification(testSession, "Test certification content.");

        when(dataService.deleteCertification(testSessionId, testCertification.getId()))
                .thenThrow(new IllegalArgumentException("Certification id not found"));

        restTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/certifications")
                        .queryParam("id", testCertification.getId())
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        verify(dataService, times(1)).deleteCertification(testSessionId, testCertification.getId());
    }
}
