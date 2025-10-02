package controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import ukma.springboot.nextskill.common.models.responses.CourseResponse;
import ukma.springboot.nextskill.common.models.responses.UserResponse;
import ukma.springboot.nextskill.course.CourseService;
import ukma.springboot.nextskill.course.SectionService;
import ukma.springboot.nextskill.course.controller.CourseController;
import ukma.springboot.nextskill.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CourseControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseExternalAPI;

    @Mock
    private SectionService sectionExternalAPI;

    @Mock
    private Model model;

    @InjectMocks
    private CourseController courseController;

    private UserResponse mockUser;
    private CourseResponse mockCourse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock data
        mockUser = UserResponse.builder()
                .uuid(UUID.randomUUID())
                .username("test_user")
                .build();

        mockCourse = CourseResponse.builder()
                .uuid(UUID.randomUUID())
                .name("Test Course")
                .description("Test Description")
                .createdAt(LocalDateTime.now())
                .teacher(mockUser)
                .students(List.of(mockUser))
                .build();
    }

    @Test
    void testHome() {
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        String viewName = courseController.home(model);

        verify(userService).getAuthenticatedUser();
        verify(model).addAttribute("user", mockUser);

        assertEquals("home", viewName, "The returned view name should be 'home'");
    }

    @Test
    void testEnroll() {
        UUID courseUuid = mockCourse.getUuid();
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        String viewName = courseController.enroll(courseUuid, model);

        verify(userService).getAuthenticatedUser();
        verify(courseExternalAPI).enrollStudent(courseUuid, mockUser.getUuid());

        assertEquals("redirect:/course/" + courseUuid + "?enrolled", viewName, "The returned view should redirect to the course page with enrolled query.");
    }

    @Test
    void testUnroll() {
        UUID courseUuid = mockCourse.getUuid();
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        String viewName = courseController.unroll(courseUuid, model);

        verify(userService).getAuthenticatedUser();
        verify(courseExternalAPI).unrollStudent(courseUuid, mockUser.getUuid());

        assertEquals("redirect:/course/" + courseUuid + "?unrolled", viewName, "The returned view should redirect to the course page with unrolled query.");
    }
}
