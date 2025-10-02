package ukma.springboot.nextskill.course.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ukma.springboot.nextskill.common.models.responses.SectionResponse;
import ukma.springboot.nextskill.common.models.responses.UserResponse;
import ukma.springboot.nextskill.common.models.views.SectionView;
import ukma.springboot.nextskill.course.CourseService;
import ukma.springboot.nextskill.course.SectionService;
import ukma.springboot.nextskill.user.UserService;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class SectionController {

    private static final String REDIRECT_TO_COURSE = "redirect:/course/";
    private static final String SECTION = "section";
    private SectionService sectionExternalAPI;
    private UserService userService;
    private CourseService courseExternalAPI;

    @GetMapping("section/{sectionUuid}/edit")
    public String editSection(@PathVariable UUID sectionUuid, Model model) {
        model.addAttribute("user", userService.getAuthenticatedUser());
        model.addAttribute(SECTION, sectionExternalAPI.get(sectionUuid));
        return "edit-section";
    }

    @PostMapping("/section/{sectionUuid}/edit")
    public String editSection(
            @PathVariable String sectionUuid,
            @ModelAttribute SectionView sectionView
    ) {
        UUID sectionId = UUID.fromString(sectionUuid);
        SectionResponse sectionResponse = sectionExternalAPI.get(sectionId);
        UUID courseId = sectionResponse.getCourse().getUuid();

        UserResponse authenticated = userService.getAuthenticatedUser();
        boolean isOwner = courseExternalAPI.hasOwnerRights(authenticated.getUuid(), courseId);
        if (!isOwner && !userService.isAdmin(authenticated.getUuid()))
            return REDIRECT_TO_COURSE + courseId;

        sectionView.setUuid(sectionId);
        sectionExternalAPI.update(sectionView);

        return REDIRECT_TO_COURSE + courseId + "?section&updated";
    }

    @PostMapping("section/{sectionUuid}/delete")
    public String deleteSection(@PathVariable UUID sectionUuid) {
        SectionResponse section = sectionExternalAPI.get(sectionUuid);
        sectionExternalAPI.delete(sectionUuid);
        return REDIRECT_TO_COURSE + section.getCourse().getUuid() + "?section&deleted";
    }

    @GetMapping("section/{sectionUuid}/create-test")
    public String createTestInSection(@PathVariable UUID sectionUuid, Model model) {
        model.addAttribute("user", userService.getAuthenticatedUser());
        model.addAttribute(SECTION, sectionExternalAPI.get(sectionUuid));
        return "add-test";
    }

    @GetMapping("section/{sectionUuid}/create-post")
    public String createPostInSection(@PathVariable UUID sectionUuid, Model model) {
        model.addAttribute("user", userService.getAuthenticatedUser());
        model.addAttribute(SECTION, sectionExternalAPI.get(sectionUuid));
        return "add-post";
    }
}
