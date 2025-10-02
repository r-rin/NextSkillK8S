package ukma.springboot.nextskill.post.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ukma.springboot.nextskill.common.models.enums.UserRole;
import ukma.springboot.nextskill.common.models.responses.PostResponse;
import ukma.springboot.nextskill.common.models.responses.SectionResponse;
import ukma.springboot.nextskill.common.models.responses.UserResponse;
import ukma.springboot.nextskill.common.models.views.PostView;
import ukma.springboot.nextskill.course.CourseService;
import ukma.springboot.nextskill.course.SectionService;
import ukma.springboot.nextskill.post.PostService;
import ukma.springboot.nextskill.user.UserService;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class PostController {

    private static final String REDIRECT_TO_COURSE = "redirect:/course";
    private PostService postService;
    private CourseService courseExternalAPI;
    private UserService userService;
    private SectionService sectionExternalAPI;

    @PostMapping("/post/{postUuid}/delete")
    public String deleteTest(@PathVariable String postUuid) {
        UUID postId = UUID.fromString(postUuid);
        UUID courseId = postService.get(postId).getSection().getCourse().getUuid();

        UserResponse authenticated = userService.getAuthenticatedUser();
        boolean isOwner = courseExternalAPI.hasOwnerRights(authenticated.getUuid(), courseId);
        if (!isOwner && authenticated.getRole() != UserRole.ADMIN)
            return REDIRECT_TO_COURSE + courseId;

        postService.delete(postId);

        return "redirect:/course/" + courseId + "?post&deleted";
    }

    @PostMapping("/post/add")
    public String addPostToSection(@ModelAttribute PostView postView) {
        SectionResponse associatedSection = sectionExternalAPI.get(postView.getSectionId());
        UserResponse authenticated = userService.getAuthenticatedUser();

        if ( authenticated.getRole() != UserRole.ADMIN &&
                !associatedSection.getCourse().getTeacher().getUuid().equals(authenticated.getUuid())
        ) {
            return "redirect:/home";
        }

        PostView view = PostView.builder()
                .sectionId(postView.getSectionId())
                .name(postView.getName())
                .content(postView.getContent())
                .isHidden(postView.getIsHidden())
                .build();

        PostResponse res = postService.create(view);

        return "redirect:/post/" + res.getUuid();
    }

    @GetMapping("/post/{postId}")
    public String getPostById(@PathVariable UUID postId, Model model) {
        UserResponse authenticated = userService.getAuthenticatedUser();
        PostResponse postResponse = postService.get(postId);
        model.addAttribute("user",authenticated);
        model.addAttribute("post", postResponse);
        return "post";
    }

    @GetMapping("/post/{postUuid}/edit")
    public String getPostEditView(
            @PathVariable(name="postUuid") UUID postId,
            Model model
    ) {
        PostResponse post = postService.get(postId);

        SectionResponse associatedSection = sectionExternalAPI.get(post.getSection().getUuid());
        UserResponse authenticated = userService.getAuthenticatedUser();

        if ( authenticated.getRole() != UserRole.ADMIN &&
                !associatedSection.getCourse().getTeacher().getUuid().equals(authenticated.getUuid())
        ) {
            return REDIRECT_TO_COURSE+ associatedSection.getCourse().getUuid();
        }

        model.addAttribute("post", post);

        return "edit-post";
    }

    @PostMapping("/post/{postUuid}/edit")
    public String addPostToSection(
            @PathVariable(name="postUuid") UUID postId,
            @ModelAttribute PostView postView
    ) {
        SectionResponse associatedSection = sectionExternalAPI.get(postView.getSectionId());
        UserResponse authenticated = userService.getAuthenticatedUser();

        if ( authenticated.getRole() != UserRole.ADMIN &&
                !associatedSection.getCourse().getTeacher().getUuid().equals(authenticated.getUuid())
        ) {
            return "redirect:/home";
        }

        PostView view = PostView.builder()
                .uuid(postId)
                .sectionId(postView.getSectionId())
                .name(postView.getName())
                .content(postView.getContent())
                .isHidden(postView.getIsHidden())
                .build();

        PostResponse res = postService.update(view);

        return "redirect:/post/" + res.getUuid();
    }
}
