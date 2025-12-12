package org.gateway;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

//import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "services.storage.base-url=http://storage",
        "services.analysis.base-url=http://analysis"
})
@AutoConfigureMockMvc
class GatewayControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RestTemplate restTemplate;

    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void upload_should_call_storage_then_analysis_and_return_aggregated_response() throws Exception {
        String storageResp = """
                {
                  "id":"submission-123",
                  "studentName":"Alice",
                  "studentIdentifier":"alice01",
                  "assignmentId":"HW1",
                  "originalFileName":"hello.txt",
                  "sizeBytes":12,
                  "uploadedAt":"2025-12-12T01:21:53.2792218",
                  "hasStorageInfo":true
                }
                """;

        String analysisResp = """
                {
                  "id":"report-1",
                  "submissionId":"submission-123",
                  "assignmentId":"HW1",
                  "studentIdentifier":"alice01",
                  "plagiarized":false,
                  "plagiarizedWithSubmissionId":null,
                  "analyzedAt":"2025-12-12T01:22:00"
                }
                """;

        // 1) gateway -> storage POST
        server.expect(requestTo("http://storage/internal/submissions"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(
                        org.springframework.test.web.client.match.MockRestRequestMatchers.header(
                                "Content-Type",
                                Matchers.containsString("multipart/form-data")
                        )
                )
                .andRespond(withStatus(org.springframework.http.HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(storageResp));

        // 2) gateway -> analysis POST
        server.expect(requestTo("http://analysis/internal/analysis"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(
                        org.springframework.test.web.client.match.MockRestRequestMatchers.content()
                                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                )
                .andExpect(
                        org.springframework.test.web.client.match.MockRestRequestMatchers.content()
                                .string(Matchers.containsString("\"submissionId\":\"submission-123\""))
                )
                .andRespond(withStatus(org.springframework.http.HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(analysisResp));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                "text/plain",
                "Hello world!".getBytes(StandardCharsets.UTF_8)
        );

        // meta part is STRING JSON (Swagger-friendly)
        MockMultipartFile meta = new MockMultipartFile(
                "meta",
                "",
                "text/plain",
                "{\"studentName\":\"Alice\",\"studentIdentifier\":\"alice01\",\"assignmentId\":\"HW1\"}"
                        .getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/submissions")
                        .file(file)
                        .file(meta)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.storage.id").value("submission-123"))
                .andExpect(jsonPath("$.analysis.submissionId").value("submission-123"))
                .andExpect(jsonPath("$.analysis.plagiarized").value(false));

        server.verify();
    }

    @Test
    void getSubmission_should_proxy_to_storage() throws Exception {
        server.expect(requestTo("http://storage/internal/submissions/submission-123"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":\"submission-123\"}", MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/submissions/submission-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("submission-123"));

        server.verify();
    }

    @Test
    void getAnalysis_should_proxy_to_analysis() throws Exception {
        server.expect(requestTo("http://analysis/internal/reports/submissions/submission-123"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("{\"submissionId\":\"submission-123\",\"plagiarized\":false}",
                        MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/submissions/submission-123/analysis"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.submissionId").value("submission-123"))
                .andExpect(jsonPath("$.plagiarized").value(false));

        server.verify();
    }

    @Test
    void getReports_should_proxy_to_analysis() throws Exception {
        server.expect(requestTo("http://analysis/internal/works/HW1/reports"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess("{\"assignmentId\":\"HW1\",\"reports\":[]}",
                        MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/assignments/HW1/reports"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.assignmentId").value("HW1"))
                .andExpect(jsonPath("$.reports").isArray());

        server.verify();
    }
}
