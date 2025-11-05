package ukma.springboot.nextskill.assessment;

import ukma.springboot.nextskill.common.dto.responses.QuestionAnswerResponse;
import ukma.springboot.nextskill.common.dto.views.QuestionAnswerView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AnswerService {
    List<QuestionAnswerResponse> getAll();
    QuestionAnswerResponse get(UUID id);
    QuestionAnswerResponse create(QuestionAnswerView view);
    QuestionAnswerResponse update(QuestionAnswerView view);
    void delete(UUID id);

    void updateSavedAnswers(Map<String, String> map, UUID attemptId);
}
