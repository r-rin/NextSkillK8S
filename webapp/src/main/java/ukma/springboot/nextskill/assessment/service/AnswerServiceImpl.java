package ukma.springboot.nextskill.assessment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ukma.springboot.nextskill.assessment.AnswerService;
import ukma.springboot.nextskill.assessment.repository.AnswerRepository;
import ukma.springboot.nextskill.assessment.repository.OptionRepository;
import ukma.springboot.nextskill.common.exceptions.ResourceNotFoundException;
import ukma.springboot.nextskill.common.models.entities.QuestionAnswerEntity;
import ukma.springboot.nextskill.common.models.entities.QuestionOptionEntity;
import ukma.springboot.nextskill.common.models.mappers.QuestionAnswerMapper;
import ukma.springboot.nextskill.common.models.responses.QuestionAnswerResponse;
import ukma.springboot.nextskill.common.models.views.QuestionAnswerView;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private static final String QUESTION_ANSWER = "QuestionAnswer";
    private final AnswerRepository answerRepository;
    private final OptionRepository optionRepository;

    @Override
    public List<QuestionAnswerResponse> getAll() {
        return answerRepository.findAll()
                .stream()
                .map(QuestionAnswerMapper::toQuestionAnswerResponse)
                .toList();
    }

    @Override
    public QuestionAnswerResponse get(UUID id) {
        QuestionAnswerEntity questionAnswerEntity = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(QUESTION_ANSWER, id));
        return QuestionAnswerMapper.toQuestionAnswerResponse(questionAnswerEntity);
    }

    @Override
    public QuestionAnswerResponse create(QuestionAnswerView view) {
        QuestionAnswerEntity questionAnswerEntity = answerRepository.save(
                QuestionAnswerMapper.toQuestionAnswerEntity(view)
        );
        return QuestionAnswerMapper.toQuestionAnswerResponse(questionAnswerEntity);
    }

    @Override
    public QuestionAnswerResponse update(QuestionAnswerView view) {
        QuestionAnswerEntity existingEntity = answerRepository.findById(view.getId())
                .orElseThrow(() -> new ResourceNotFoundException(QUESTION_ANSWER, view.getId()));
        QuestionAnswerEntity updatedEntity = answerRepository.save(
                QuestionAnswerMapper.mergeData(view, existingEntity)
        );
        return QuestionAnswerMapper.toQuestionAnswerResponse(updatedEntity);
    }



    @Override
    public void delete(UUID id) {
        if (answerRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(QUESTION_ANSWER, id);
        }
        answerRepository.deleteById(id);
    }

    @Override
    public void updateSavedAnswers(Map<String, String> map, UUID attemptId) {
        List<QuestionAnswerEntity> answers = answerRepository.findByTestAttemptUuid(attemptId);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String questionId = entry.getKey();
            String optionId = entry.getValue();

            Optional<QuestionAnswerEntity> existingAnswer = answers.stream()
                    .filter(answer -> answer.getQuestion().getId().toString().equals(questionId))
                    .findFirst();

            if (existingAnswer.isPresent()) {
                Optional<QuestionOptionEntity> option = optionRepository.findById(UUID.fromString(optionId));

                if (option.isPresent()) {
                    QuestionAnswerEntity answerEntity = existingAnswer.get();
                    answerEntity.setAnswerOption(option.get());

                    answerRepository.save(answerEntity);
                }
            } else {
                QuestionAnswerView answer = QuestionAnswerView.builder()
                        .questionId(UUID.fromString(questionId))
                        .questionOptionId(UUID.fromString(optionId))
                        .testAttemptId(attemptId)
                        .build();

                this.create(answer);
            }
        }
    }
}
