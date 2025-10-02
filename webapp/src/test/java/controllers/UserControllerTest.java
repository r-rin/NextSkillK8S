package controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import ukma.springboot.nextskill.common.models.responses.UserResponse;
import ukma.springboot.nextskill.common.models.views.UserView;
import ukma.springboot.nextskill.user.UserService;
import ukma.springboot.nextskill.user.controller.UserController;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    private UserResponse mockUser;
    private UUID mockUserId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUserId = UUID.randomUUID();
        mockUser = UserResponse.builder()
                .uuid(mockUserId)
                .name("Test User")
                .email("test@example.com")
                .build();
    }

    @Test
    void testProfile() {
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);
        when(userService.getWithCourses(mockUserId)).thenReturn(mockUser);

        String viewName = userController.profile(model);

        verify(userService).getAuthenticatedUser();
        verify(userService).getWithCourses(mockUserId);
        verify(model).addAttribute("currentUser", mockUser);
        verify(model).addAttribute("user", mockUser);
        assertEquals("profile", viewName, "The returned view name should be 'profile'");
    }

    @Test
    void testUpdateUser() {
        UserView userView = UserView.builder()
                .uuid(mockUserId)
                .name("Updated User")
                .email("updated@example.com")
                .build();

        String viewName = userController.updateUser(mockUserId, userView);

        verify(userService).update(userView);
        assertEquals("redirect:/profile", viewName, "The returned view name should redirect to '/profile'");
    }

    @Test
    void testDeleteUser() {
        String viewName = userController.deleteUser(mockUserId);

        verify(userService).delete(mockUserId);
        assertEquals("redirect:/home?user&deleted", viewName, "The returned view name should redirect to '/home?user&deleted'");
    }
}
