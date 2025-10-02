package ukma.springboot.nextskill.common.models.mappers;

import ukma.springboot.nextskill.common.models.entities.TestAttemptEntity;
import ukma.springboot.nextskill.common.models.entities.TestEntity;
import ukma.springboot.nextskill.common.models.entities.UserEntity;
import ukma.springboot.nextskill.common.models.responses.TestAttemptResponse;
import ukma.springboot.nextskill.common.models.views.TestAttemptView;

public class TestAttemptMapper {

    private TestAttemptMapper() {}

    public static TestAttemptResponse toTestAttemptResponse(TestAttemptEntity attemptEntity) {
        return TestAttemptResponse.builder()
                .uuid(attemptEntity.getUuid())
                .startTime(attemptEntity.getStartTime())
                .endTime(attemptEntity.getEndTime())
                .submitted(attemptEntity.isSubmitted())
                .completedBy(UserMapper.toUserResponse(attemptEntity.getCompletedBy()))
                .answers(MapperUtility.mapIfInitialized(attemptEntity.getAnswers(), QuestionAnswerMapper::toQuestionAnswerResponse))
                .build();
    }

    public static TestAttemptEntity toTestAttemptEntity(TestAttemptView view) {
        return TestAttemptEntity.builder()
                .uuid(view.getUuid())
                .startTime(view.getStartTime())
                .endTime(view.getEndTime())
                .submitted(view.isSubmitted())
                .completedBy(UserEntity.builder().uuid(view.getCompletedById()).build())
                .test(TestEntity.builder().uuid(view.getTestId()).build())
                .build();
    }

    public static TestAttemptEntity mergeData(TestAttemptView view, TestAttemptEntity entity) {
        return TestAttemptEntity.builder()
                .uuid(entity.getUuid())
                .startTime(entity.getStartTime())
                .endTime(MapperUtility.orElse(view.getEndTime(), entity.getEndTime()))
                .submitted(MapperUtility.orElse(view.isSubmitted(), entity.isSubmitted()))
                .completedBy(entity.getCompletedBy())
                .test(entity.getTest())
                .build();
    }
}
