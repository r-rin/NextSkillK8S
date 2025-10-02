package ukma.springboot.nextskill.assessment.controller;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ukma.springboot.nextskill.assessment.OptionDeletedEvent;
import ukma.springboot.nextskill.assessment.OptionService;
import ukma.springboot.nextskill.assessment.QuestionService;
import ukma.springboot.nextskill.assessment.TestService;
import ukma.springboot.nextskill.common.models.enums.UserRole;
import ukma.springboot.nextskill.common.models.responses.QuestionOptionResponse;
import ukma.springboot.nextskill.common.models.responses.QuestionResponse;
import ukma.springboot.nextskill.common.models.responses.TestResponse;
import ukma.springboot.nextskill.common.models.responses.UserResponse;
import ukma.springboot.nextskill.common.models.views.QuestionOptionView;
import ukma.springboot.nextskill.user.UserService;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class OptionController {

    private static final String REDIRECT_TO_QUESTION = "redirect:/question/";
    private static final String MANAGE_OPTIONS = "/manage-options";
    private QuestionService questionExternalAPI;
    private OptionService optionService;
    private UserService userService;
    private TestService testService;
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/option/{optionUuid}/set-correct")
    public ResponseEntity<String> setCorrect(
            @PathVariable(name = "optionUuid") String optionUuid
    ) {
        UUID optionId = UUID.fromString(optionUuid);

        QuestionResponse associatedQuestion = questionExternalAPI.getQuestionByOption(optionId);
        TestResponse associatedTest = testService.getTestByQuestion(associatedQuestion.getId());

        UserResponse authenticated = userService.getAuthenticatedUser();
        boolean isOwner = testService.hasOwnerRights(authenticated.getUuid(), associatedTest.getUuid());
        if(!isOwner && authenticated.getRole() != UserRole.ADMIN)
            return ResponseEntity.badRequest().body("No access.");

        optionService.setNewCorrect(associatedQuestion.getId(), optionId);

        return ResponseEntity.ok("Success!");
    }

    @PostMapping("/option/{optionUuid}/delete")
    public String deleteOption(
            @PathVariable(name = "optionUuid") String optionUuid
    ) {
        UUID optionId = UUID.fromString(optionUuid);

        QuestionResponse associatedQuestion = questionExternalAPI.getQuestionByOption(optionId);
        TestResponse associatedTest = testService.getTestByQuestion(associatedQuestion.getId());

        UserResponse authenticated = userService.getAuthenticatedUser();
        boolean isOwner = testService.hasOwnerRights(authenticated.getUuid(), associatedTest.getUuid());
        if(!isOwner && authenticated.getRole() != UserRole.ADMIN)
            return REDIRECT_TO_QUESTION+ associatedQuestion.getId() + MANAGE_OPTIONS;

        eventPublisher.publishEvent(new OptionDeletedEvent(this, associatedTest.getUuid(), optionId));
        optionService.delete(optionId);

        return REDIRECT_TO_QUESTION+ associatedQuestion.getId() + MANAGE_OPTIONS;
    }

    @PostMapping("/option/{optionUuid}/edit")
    public String editOption(
            @PathVariable(name = "optionUuid") String optionUuid,
            @ModelAttribute QuestionOptionView optionView
    ) {
        UUID optionId = UUID.fromString(optionUuid);

        QuestionResponse associatedQuestion = questionExternalAPI.getQuestionByOption(optionId);
        TestResponse associatedTest = testService.getTestByQuestion(associatedQuestion.getId());

        UserResponse authenticated = userService.getAuthenticatedUser();
        boolean isOwner = testService.hasOwnerRights(authenticated.getUuid(), associatedTest.getUuid());
        if(!isOwner && authenticated.getRole() != UserRole.ADMIN)
            return REDIRECT_TO_QUESTION+ associatedQuestion.getId() + MANAGE_OPTIONS;

        QuestionOptionResponse res = optionService.update(optionView);
        if (optionView.isCorrect()) {
            optionService.setNewCorrect(associatedQuestion.getId(), res.getId());
        }

        return REDIRECT_TO_QUESTION+ associatedQuestion.getId() + MANAGE_OPTIONS;
    }

    @PostMapping("/option/add")
    public String addOption(
            @ModelAttribute QuestionOptionView optionView
    ) {
        TestResponse associatedTest = testService.getTestByQuestion(optionView.getQuestionId());

        UserResponse authenticated = userService.getAuthenticatedUser();
        boolean isOwner = testService.hasOwnerRights(authenticated.getUuid(), associatedTest.getUuid());
        if(!isOwner && authenticated.getRole() != UserRole.ADMIN)
            return REDIRECT_TO_QUESTION+ optionView.getQuestionId() + MANAGE_OPTIONS;

        QuestionOptionView view = QuestionOptionView.builder()
                .questionId(optionView.getQuestionId())
                .optionText(optionView.getOptionText())
                .isCorrect(optionView.isCorrect())
                .build();

        QuestionOptionResponse res = optionService.create(view);
        if (res.isCorrect()) {
            optionService.setNewCorrect(optionView.getQuestionId(), res.getId());
        }

        return REDIRECT_TO_QUESTION+ optionView.getQuestionId() + MANAGE_OPTIONS;
    }
}
