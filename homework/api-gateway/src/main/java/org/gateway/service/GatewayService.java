package org.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gateway.dto.SubmissionFullResponse;
import org.gateway.dto.SubmissionUploadDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GatewayService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String storageUrl;
    private final String analysisUrl;

    public GatewayService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${services.storage.base-url}") String storageUrl,
            @Value("${services.analysis.base-url}") String analysisUrl
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.storageUrl = storageUrl;
        this.analysisUrl = analysisUrl;
    }

    public SubmissionFullResponse uploadAndAnalyze(
            byte[] fileBytes,
            String filename,
            String metaJson
    ) throws Exception {

        SubmissionUploadDto meta =
                objectMapper.readValue(metaJson, SubmissionUploadDto.class);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        });

        HttpHeaders metaHeaders = new HttpHeaders();
        metaHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> metaPart = new HttpEntity<>(metaJson, metaHeaders);
        body.add("meta", metaPart);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> storageResp =
                restTemplate.postForEntity(
                        storageUrl + "/internal/submissions",
                        request,
                        Map.class
                );

        Object storageBody = storageResp.getBody();
        String submissionId = storageResp.getBody().get("id").toString();

        Map<String, Object> analysisReq = Map.of(
                "submissionId", submissionId,
                "assignmentId", meta.assignmentId,
                "studentIdentifier", meta.studentIdentifier
        );

        Object analysisResp =
                restTemplate.postForObject(
                        analysisUrl + "/internal/analysis",
                        analysisReq,
                        Object.class
                );

        return new SubmissionFullResponse(storageBody, analysisResp);
    }

    public Object getSubmission(String id) {
        return restTemplate.getForObject(
                storageUrl + "/internal/submissions/" + id,
                Object.class
        );
    }

    public Object getAnalysis(String id) {
        return restTemplate.getForObject(
                analysisUrl + "/internal/reports/submissions/" + id,
                Object.class
        );
    }

    public Object getReports(String assignmentId) {
        return restTemplate.getForObject(
                analysisUrl + "/internal/works/" + assignmentId + "/reports",
                Object.class
        );
    }
}
