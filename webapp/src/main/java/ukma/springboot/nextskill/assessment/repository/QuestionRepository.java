package ukma.springboot.nextskill.assessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ukma.springboot.nextskill.common.models.entities.QuestionEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<QuestionEntity, UUID> {
    List<QuestionEntity> findByTestUuid(UUID testUuid);
    Optional<QuestionEntity> findQuestionEntityByQuestionOptionsId(UUID questionOptionsId);
}
