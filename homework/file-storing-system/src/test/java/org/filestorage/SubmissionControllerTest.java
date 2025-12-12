package org.filestorage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SubmissionControllerTest extends TestContainersConfig {

    @Autowired
    MockMvc mockMvc;

    @Test
    void testUploadAndFetchSubmission() throws Exception {

        // 1. create mock file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                "text/plain",
                "Hello world!".getBytes()
        );

        // 2. create JSON metadata
        MockMultipartFile meta = new MockMultipartFile(
                "meta",
                "",
                "application/json",
                """
                {
                    "studentName": "Alice",
                    "studentIdentifier": "alice01",
                    "assignmentId": "HW1"
                }
                """.getBytes()
        );

        // 3. Perform upload
        var uploadResult = mockMvc.perform(
                        multipart("/internal/submissions")
                                .file(file)
                                .file(meta)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentName").value("Alice"))
                .andExpect(jsonPath("$.originalFileName").value("hello.txt"))
                .andReturn();

        // Extract ID from response
        String body = uploadResult.getResponse().getContentAsString();
        String id = body.substring(body.indexOf("id\":\"") + 5, body.indexOf("\",\"studentName"));

        // 4. Fetch submission metadata
        mockMvc.perform(get("/internal/submissions/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentIdentifier").value("alice01"))
                .andExpect(jsonPath("$.assignmentId").value("HW1"));

        // 5. Fetch file
        mockMvc.perform(get("/internal/submissions/" + id + "/file"))
                .andExpect(status().isOk())
                .andExpect(content().bytes("Hello world!".getBytes()));
    }
}