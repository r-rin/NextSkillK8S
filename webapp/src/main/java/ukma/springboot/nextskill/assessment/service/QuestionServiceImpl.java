package ukma.springboot.nextskill.assessment.service;

import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import ukma.springboot.nextskill.assessment.QuestionService;
import ukma.springboot.nextskill.assessment.repository.QuestionRepository;
import ukma.springboot.nextskill.common.exceptions.ResourceNotFoundException;
import ukma.springboot.nextskill.common.models.entities.QuestionEntity;
import ukma.springboot.nextskill.common.models.mappers.QuestionMapper;
import ukma.springboot.nextskill.common.models.responses.QuestionResponse;
import ukma.springboot.nextskill.common.models.views.QuestionView;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private static final String QUESTION = "Question";
    private final QuestionRepository questionRepository;

    @Override
    public List<QuestionResponse> getAll() {
        return questionRepository.findAll()
                .stream()
                .map(QuestionMapper::toQuestionResponse)
                .toList();
    }

    @Override
    public QuestionResponse get(UUID id) {
        QuestionEntity questionEntity = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(QUESTION, id));
        Hibernate.initialize(questionEntity.getQuestionOptions());
        return QuestionMapper.toQuestionResponse(questionEntity);
    }

    @Override
    public QuestionResponse create(QuestionView view) {
        QuestionEntity questionEntity = questionRepository.save(
                QuestionMapper.toQuestionEntity(view)
        );
        return QuestionMapper.toQuestionResponse(questionEntity);
    }

    @Override
    public QuestionResponse update(QuestionView view) {
        QuestionEntity existingEntity = questionRepository.findById(view.getId())
                .orElseThrow(() -> new ResourceNotFoundException(QUESTION, view.getId()));
        QuestionEntity updatedEntity = questionRepository.save(
                QuestionMapper.mergeData(view, existingEntity)
        );
        return QuestionMapper.toQuestionResponse(updatedEntity);
    }

    @Override
    public void delete(UUID id) {
        if (questionRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(QUESTION, id);
        }
        questionRepository.deleteById(id);
    }

    @Override
    public List<QuestionResponse> getTestQuestions(UUID testId) {
        return questionRepository.findByTestUuid(testId)
                .stream()
                .map(question -> {
                    Hibernate.initialize(question.getQuestionOptions());
                    return QuestionMapper.toQuestionResponse(question);
                })
                .toList();
    }

    @Override
    public QuestionResponse getQuestionByOption(UUID optionId) {
        QuestionEntity questionEntity = questionRepository.findQuestionEntityByQuestionOptionsId(optionId)
                .orElseThrow(() -> new ResourceNotFoundException(QUESTION, optionId));
        return QuestionMapper.toQuestionResponse(questionEntity);
    }
}
