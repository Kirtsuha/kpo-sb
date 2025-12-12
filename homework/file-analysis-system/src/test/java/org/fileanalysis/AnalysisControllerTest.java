package org.fileanalysis;

import org.fileanalysis.repository.AnalysisReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AnalysisControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AnalysisReportRepository reportRepository;

    @org.springframework.boot.test.mock.mockito.MockBean
    RestTemplate restTemplate;

    @BeforeEach
    void cleanDb() {
        reportRepository.deleteAll();
    }

    @Test
    void full_flow_detects_plagiarism_for_same_text() throws Exception {
        // GIVEN: два submissionId с одинаковым содержимым
        String assignmentId = "HW1";
        String sub1 = "submission-1";
        String sub2 = "submission-2";

        String text = "int main() { return 0; }";

        // мокнем RestTemplate, чтобы сервис не ходил в реальный file-storage-service
        String baseUrl = "http://localhost:8081"; // такой же как storage.base-url в application.yml

        when(restTemplate.getForObject(
                eq(baseUrl + "/internal/submissions/" + sub1 + "/file"),
                eq(byte[].class))
        ).thenReturn(text.getBytes(StandardCharsets.UTF_8));

        when(restTemplate.getForObject(
                eq(baseUrl + "/internal/submissions/" + sub2 + "/file"),
                eq(byte[].class))
        ).thenReturn(text.getBytes(StandardCharsets.UTF_8));

        // WHEN: анализируем первую сдачу — плагиата ещё быть не должно
        mockMvc.perform(
                        post("/internal/analysis")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "submissionId": "submission-1",
                                          "assignmentId": "HW1",
                                          "studentIdentifier": "alice01"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.submissionId").value(sub1))
                .andExpect(jsonPath("$.assignmentId").value(assignmentId))
                .andExpect(jsonPath("$.studentIdentifier").value("alice01"))
                .andExpect(jsonPath("$.plagiarized").value(false));

        // AND: анализируем вторую сдачу с тем же текстом → должна быть плагиатом
        mockMvc.perform(
                        post("/internal/analysis")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "submissionId": "submission-2",
                                          "assignmentId": "HW1",
                                          "studentIdentifier": "bob01"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.submissionId").value(sub2))
                .andExpect(jsonPath("$.plagiarized").value(true))
                .andExpect(jsonPath("$.plagiarizedWithSubmissionId").value(sub1));

        // THEN: можно получить отчёт по конкретной сдаче
        mockMvc.perform(get("/internal/reports/submissions/" + sub2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submissionId").value(sub2))
                .andExpect(jsonPath("$.plagiarized").value(true))
                .andExpect(jsonPath("$.plagiarizedWithSubmissionId").value(sub1));

        // AND: можно получить все отчёты по заданию
        mockMvc.perform(get("/internal/works/" + assignmentId + "/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentId").value(assignmentId))
                .andExpect(jsonPath("$.reports.length()").value(2));
    }
}
