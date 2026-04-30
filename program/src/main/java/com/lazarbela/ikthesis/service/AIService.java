package com.lazarbela.ikthesis.service;

import com.lazarbela.ikthesis.model.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIService {

    public static final Object resumePreviewWaitObject = new Object();

    private final String extractLinkUri = "http://localhost:7860/api/v1/run/extract-linkedin/";
    private final String extractFileUri = "http://localhost:7860/api/v1/run/extract-file?stream=false";
    private final String makeResumeUri = "http://localhost:7860/api/v1/run/make-resume?stream=false";
    private final String apiKey = "sk-T1tTJMRUyN5JshzFyGZTe0KGQMP7-UzU5eDGqtVYa54";
    private final DataService dataService;
    private final FileService fileService;
    private final SessionService sessionService;
    private final RestTemplate restTemplate = new RestTemplate();

    public AIService(DataService dataService, FileService fileService, SessionService sessionService)
    {
        this.dataService = dataService;
        this.fileService = fileService;
        this.sessionService = sessionService;
    }

//    public String extractLinkedInLink (Session session, String link)
//    {
//
//        String payload = "{ \"output_type\":\"text\",\"input_type\":\"text\",\"tweaks\":{\"TextInput-WcTsY\":{\"input_value\":"+link+"\"}}}";
//        String dataItems = restClient
//                .post()
//                .uri(extractLinkUri)
//                .header("x-api-key", apiKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(payload)
//                .retrieve()
//                .toString();
//
//        //return saveFromReturnData(session, dataItems);
//        return "";
//    }

    public String extractFiles (Session session) throws IOException
    {
        Set<String> filePaths = dataService.getFiles(session.getSessionId()).stream().map(fileMetadata -> {
            try {
                return fileService.getFileResource(fileMetadata.getStoredName())
                        .getFilePath()
                        .toAbsolutePath()
                        .toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());

        for(String path : filePaths) {

            Map<String, Object> payload = Map.of(
                    "TextInput-xh0lP", Map.of(
                            "input_value", path
                    )
            );

            ResponseEntity<String> response = postRequest(extractFileUri, session.getSessionId(), payload);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode resultOutput = mapper.readTree(response.getBody());
            String list = resultOutput
                    .get("outputs")
                    .get(0)
                    .get("outputs")
                    .get(0)
                    .get("outputs")
                    .get("text")
                    .get("message").stringValue();

            List<HashMap<String, String>> itemsMap = mapper.readValue(list, List.class);

            List<ReturnDataItem> dataItems = itemsMap.stream().map(item -> new ReturnDataItem(item.get("Type"), item.get("Content"))).toList();
            saveFromReturnData(session, dataItems);
        }

        return MessageFormat.format("Done with {0} files", filePaths.size());
    }

    private ResponseEntity<String> postRequest(String uri, String sessionId, Map<String, Object> valuablePayload)
    {
        Map<String, Object> payload = Map.of(
                "output_type","text",
                "input_type","text",
                "session_id",sessionId,
                "tweaks", valuablePayload
        );

        ObjectMapper mapper = new ObjectMapper();
        String bodyString = mapper.writeValueAsString(payload);

        HttpHeaders header = new HttpHeaders();
        header.add("x-api-key", apiKey);
        header.add("Content-Type", "application/json");
        header.add("Accept", "*/*");
        header.add("Connection", "keep-alive");

        HttpEntity<String> httpRequest = new HttpEntity<String>(bodyString, header);

        return restTemplate.postForEntity(uri, httpRequest , String.class);
    }


    private String saveFromReturnData(Session session, List<ReturnDataItem> returnDataItems)
    {
        if( returnDataItems == null)
        {
            throw new NullPointerException("return ");
        }
        int ud = 0;
        int c = 0;
        int e = 0;
        int we = 0;
        int of = 0;
        int err = 0;
        for(ReturnDataItem item : returnDataItems)
        {
            switch(item.Type())
            {
                case "name":
                    UserData userData = session.getUserData();
                    if(userData == null){
                        userData = new UserData(session);
                    }
                    userData.setName(item.Content());
                    dataService.saveUserData(userData);
                    ud++;
                    break;
                case "certification":
                    Certification newCertification = new Certification(session, item.Content());
                    dataService.saveCertification(newCertification);
                    c++;
                    break;
                case "education":
                    Education newEducation = new Education(session, item.Content());
                    dataService.saveEducation(newEducation);
                    e++;
                    break;
                case "other-field":
                    OtherField newOtherField = new OtherField(session, item.Content());
                    dataService.saveOtherField(newOtherField);
                    of++;
                    break;
                case "work-experience":
                    WorkExperience newWorkExperience = new WorkExperience(session, item.Content());
                    dataService.saveWorkExperience(newWorkExperience);
                    we++;
                    break;
                default:
                    System.err.println("Wrong field type at AI extraction: \"" + item.Type() + "\"");
                    err++;
                    break;
            }
        }
        return(MessageFormat.format("Of {0} entries: {1} user data, {2} certification, {3} education, {4} other field, {5} work experience, with {6} not matching", returnDataItems.size(), ud, c, e, of, we, err));
    }

    public String createResume(Session session)
    {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> payload = Map.of(
                "TextInput-u0oMD", Map.of(
                        "input_value", mapper.writeValueAsString(session)
                )
        );
        ResponseEntity<String> response = postRequest(makeResumeUri, session.getSessionId(), payload);

        JsonNode resultOutput = mapper.readTree(response.getBody());
        String resultMarkdown = resultOutput
                .get("outputs")
                .get(0)
                .get("outputs")
                .get(0)
                .get("outputs")
                .get("text")
                .get("message").stringValue();

        String resultHtml = null;

        try {
            FileMetadata savedMarkdownResume = fileService.saveResume(new ByteArrayInputStream(resultMarkdown.getBytes()), session.getSessionId(), "md", resultMarkdown.getBytes().length);
            // convert and save other formats as well

        }
        catch (IOException e){
            throw new RuntimeException("Could not write output to file");
        }

        if(resultHtml != null)
        {
            session.setResumePreviewString(resultHtml);
            sessionService.saveSession(session);
        }
        else {
            session.setResumePreviewString(resultMarkdown);
            sessionService.saveSession(session);
        }

        synchronized (AIService.resumePreviewWaitObject){
            AIService.resumePreviewWaitObject.notify();
        }

        return resultMarkdown;
    }
}
