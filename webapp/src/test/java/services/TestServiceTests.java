package services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ukma.springboot.nextskill.assessment.repository.TestRepository;
import ukma.springboot.nextskill.assessment.service.TestServiceImpl;
import ukma.springboot.nextskill.common.models.entities.CourseEntity;
import ukma.springboot.nextskill.common.models.entities.SectionEntity;
import ukma.springboot.nextskill.common.models.entities.TestEntity;
import ukma.springboot.nextskill.common.models.entities.UserEntity;
import ukma.springboot.nextskill.common.models.responses.UserResponse;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestServiceTests {

    @Mock
    private TestRepository testRepository;

    @InjectMocks
    private TestServiceImpl testService;

    @Test
    void testDeleteTest() {
        UUID testId = UUID.randomUUID();
        TestEntity testEntity = new TestEntity();
        testEntity.setUuid(testId);

        when(testRepository.findById(testId)).thenReturn(Optional.of(testEntity));

        assertDoesNotThrow(() -> testService.delete(testId));
        verify(testRepository, times(1)).deleteById(testId);
    }

    @Test
    void testHasOwnerRights() {
        UUID userId = UUID.randomUUID();
        UUID testId = UUID.randomUUID();
        UUID courseOwnerId = userId;

        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setTeacher(new UserEntity());
        courseEntity.getTeacher().setUuid(courseOwnerId);

        TestEntity testEntity = new TestEntity();
        testEntity.setSection(new SectionEntity());
        testEntity.getSection().setCourse(courseEntity);

        when(testRepository.findById(testId)).thenReturn(Optional.of(testEntity));

        assertTrue(testService.hasOwnerRights(userId, testId));
    }

    @Test
    void testCheckTestAccess_Owner() {
        UUID testUuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserResponse userResponse = new UserResponse();
        userResponse.setUuid(userId);

        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setTeacher(new UserEntity());
        courseEntity.getTeacher().setUuid(userId);

        TestEntity testEntity = new TestEntity();
        testEntity.setSection(new SectionEntity());
        testEntity.getSection().setCourse(courseEntity);

        when(testRepository.findById(testUuid)).thenReturn(Optional.of(testEntity));

        assertDoesNotThrow(() -> testService.checkTestAccess(testUuid, userResponse));
    }
}
