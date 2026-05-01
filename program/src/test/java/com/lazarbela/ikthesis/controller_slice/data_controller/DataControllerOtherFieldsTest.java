// example: https://spring.io/guides/gs/testing-web

package com.lazarbela.ikthesis.controller_slice.data_controller;

import com.lazarbela.ikthesis.controller.DataController;
import com.lazarbela.ikthesis.model.OtherField;
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
public class DataControllerOtherFieldsTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private DataService dataService;

    @MockitoBean
    private SessionService sessionService;

    private final String baseUri = "/api/v1/data";

    private final String testSessionId = "testSessionId";

    private final Session testSession = new Session();

    private final HashSet<OtherField> testOtherFields = new LinkedHashSet<>();

    private final OtherField testOtherField1 = new OtherField(testSession, "Test content 1.");

    private final OtherField testOtherField2 = new OtherField(testSession, "Test content 2.");

    @BeforeEach
    void setUp() {
        testSession.setSessionId(testSessionId);
        testSession.setOtherFields(testOtherFields);

        testOtherFields.add(testOtherField1);
        testOtherFields.add(testOtherField2);
    }

    @Test
    void getOtherFieldsSuccessful()
    {
        when(dataService.getOtherFields(testSessionId)).thenReturn(testOtherFields);

        restTestClient
                .get().uri(baseUri + "/other-fields")
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(testOtherField1.getId())
                .jsonPath("$[0].content").isEqualTo(testOtherField1.getContent())
                .jsonPath("$[1].id").isEqualTo(testOtherField2.getId())
                .jsonPath("$[1].content").isEqualTo(testOtherField2.getContent());

        verify(dataService, times(1)).getOtherFields(testSessionId);
    }

    @Test
    void getOtherFieldsBadRequest()
    {
        when(dataService.getOtherFields("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/other-fields")
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        verify(dataService, times(1)).getOtherFields("invalidSessionId");
    }

    @Test
    void postOtherFieldSuccessful()
    {
        OtherField testOtherField = new OtherField(testSession, "Test OtherField content.");

        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveOtherField(testOtherField)).thenReturn(testOtherField);

        restTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/other-fields")
                        .queryParam("content", "Test OtherField content.")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testOtherField.getId())
                .jsonPath("$.content").isEqualTo(testOtherField.getContent());

        verify(sessionService, times(1)).getSessionById(testSessionId);
        verify(dataService, times(1)).saveOtherField(testOtherField);
    }

    @Test
    void postOtherFieldMissingQueryParamBadRequest()
    {
        OtherField testOtherField = new OtherField(testSession, "Test OtherField content.");

        when(sessionService.getSessionById(testSessionId)).thenReturn(testSession);
        when(dataService.saveOtherField(testOtherField)).thenReturn(testOtherField);

        restTestClient
                .post().uri(baseUri + "/other-fields")
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void postOtherFieldInvalidSessionIdBadRequest()
    {
        when(dataService.getOtherFields("invalidSessionId"))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .get().uri(baseUri + "/other-fields")
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        verify(dataService, times(1)).getOtherFields("invalidSessionId");
    }

    @Test
    void pathOtherFieldSuccessful()
    {
        OtherField testOtherField = new OtherField(testSession, "Test OtherField content.");
        testOtherField.setContent("Updated content 1.");

        when(dataService.updateOtherField(testSessionId, testOtherField.getId(), "Updated content 1."))
                .thenReturn(testOtherField);

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/other-fields")
                        .queryParam("id", testOtherField.getId())
                        .queryParam("content", "Updated content 1.")
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testOtherField.getId())
                .jsonPath("$.content").isEqualTo("Updated content 1.");

        verify(dataService, times(1))
                .updateOtherField(testSessionId, testOtherField.getId(),"Updated content 1.");
    }

    @Test
    void pathOtherFieldMissingIdParamBadRequest()
    {
        OtherField testOtherField = new OtherField(testSession, "Test OtherField content.");
        testOtherField.setContent("Updated content 1.");

        when(dataService.updateOtherField("invalidSessionId", testOtherField.getId(), "Updated content 1."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                    .path(baseUri + "/other-fields")
                    .queryParam("content", "Updated content 1.")
                    .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void pathOtherFieldMissingContentParamBadRequest()
    {
        OtherField testOtherField = new OtherField(testSession, "Test OtherField content.");
        testOtherField.setContent("Updated content 1.");

        when(dataService.updateOtherField("invalidSessionId", testOtherField.getId(), "Updated content 1."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/other-fields")
                        .queryParam("id", testOtherField.getId())
                        .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void pathOtherFieldInvalidSessionIdBadRequest()
    {
        OtherField testOtherField = new OtherField(testSession, "Test OtherField content.");
        testOtherField.setContent("Updated content 1.");

        when(dataService.updateOtherField("invalidSessionId", testOtherField.getId(), "Updated content 1."))
                .thenThrow(new IllegalArgumentException("Session id not found"));

        restTestClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/other-fields")
                        .queryParam("id", testOtherField.getId())
                        .queryParam("content", "Updated content 1.")
                        .build())
                .header("Cookie", "sessionId=invalidSessionId")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        verify(dataService, times(1))
                .updateOtherField("invalidSessionId", testOtherField.getId(), "Updated content 1.");
    }

    @Test
    void deleteOtherFieldsSuccessful()
    {
        OtherField testOtherField = new OtherField(testSession, "Test OtherField content.");

        when(dataService.deleteOtherField(testSessionId, testOtherField.getId())).thenReturn(testOtherField);

        restTestClient
                .delete().uri(uriBuilder -> uriBuilder
                    .path(baseUri + "/other-fields")
                    .queryParam("id", testOtherField.getId())
                    .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isNoContent();

        verify(dataService, times(1)).deleteOtherField(testSessionId, testOtherField.getId());
    }

    @Test
    void deleteOtherFieldInvalidOtherFieldIdBadRequest()
    {
        OtherField testOtherField = new OtherField(testSession, "Test OtherField content.");

        when(dataService.deleteOtherField(testSessionId, testOtherField.getId()))
                .thenThrow(new IllegalArgumentException("OtherField id not found"));

        restTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUri + "/other-fields")
                        .queryParam("id", testOtherField.getId())
                        .build())
                .header("Cookie", "sessionId=testSessionId")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        verify(dataService, times(1)).deleteOtherField(testSessionId, testOtherField.getId());
    }
}
