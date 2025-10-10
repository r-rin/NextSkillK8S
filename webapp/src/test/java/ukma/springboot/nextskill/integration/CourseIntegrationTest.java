
package ukma.springboot.nextskill.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CourseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> courseDto;

    @BeforeEach
    void setUp() {
        courseDto = new HashMap<>();
        courseDto.put("title", "Integration Test Course");
        courseDto.put("description", "This is an integration test");
        courseDto.put("authorId", 1L);
    }

    @Test
    @WithMockUser(username = "instructor@test.com", roles = {"INSTRUCTOR"})
    void testFullCourseCRUDFlow() throws Exception {
        // Create
        MvcResult createResult = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Integration Test Course"))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> createdCourse = objectMapper.readValue(responseBody, Map.class);
        Long courseId = ((Number) createdCourse.get("id")).longValue();

        // Read
        mockMvc.perform(get("/api/courses/" + courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Test Course"));

        // Update
        createdCourse.put("title", "Updated Course Title");
        mockMvc.perform(put("/api/courses/" + courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdCourse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Course Title"));

        // Verify update
        mockMvc.perform(get("/api/courses/" + courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Course Title"));

        // Delete
        mockMvc.perform(delete("/api/courses/" + courseId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/courses/" + courseId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = {"USER"})
    void testUserCannotCreateCourse() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthenticatedUserCannotAccessCourses() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isUnauthorized());
    }
}