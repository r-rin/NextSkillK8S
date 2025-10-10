package ukma.springboot.nextskill.course.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ukma.springboot.nextskill.common.models.responses.CourseResponse;
import ukma.springboot.nextskill.common.models.responses.UserResponse;
import ukma.springboot.nextskill.course.CourseService;
import ukma.springboot.nextskill.user.UserService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private UserService userService;

    private UserResponse mockUser;
    private CourseResponse mockCourse;

    @BeforeEach
    void setUp() {
        mockUser = UserResponse.builder()
                .uuid(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();

        mockCourse = CourseResponse.builder()
                .uuid(UUID.randomUUID())
                .name("Test Course")
                .description("Some description")
                .build();

        when(userService.getAuthenticatedUser()).thenReturn(mockUser);
        when(courseService.getAllWithUsers()).thenReturn(List.of(mockCourse));
        when(userService.getCourses(any())).thenReturn(List.of(mockCourse));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    void getAllCourses_shouldReturnJsonMimeType() throws Exception {
        mockMvc.perform(get("/api/all-courses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Course"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    void getCoursesForUser_shouldReturnJsonMimeType() throws Exception {
        mockMvc.perform(get("/api/courses-for-user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Course"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"STUDENT"})
    void getCoursesForUser_withAuthentication_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/courses-for-user"))
                .andExpect(status().isOk());
    }

    @Test
    void getCoursesForUser_withoutAuthentication_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/api/courses-for-user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("login")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllCourses_withAdminRole_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/all-courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    void getAllCourses_withTeacherRole_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/all-courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
