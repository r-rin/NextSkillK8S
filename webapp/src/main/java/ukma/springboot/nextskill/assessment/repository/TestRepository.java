package ukma.springboot.nextskill.assessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ukma.springboot.nextskill.common.models.entities.TestEntity;

import java.util.Optional;
import java.util.UUID;

public interface TestRepository extends JpaRepository<TestEntity, UUID> {
    Optional<TestEntity> findTestEntityByAttemptsUuid(UUID attemptUuid);
    Optional<TestEntity> findByQuestionsId(UUID questionsId);
}
