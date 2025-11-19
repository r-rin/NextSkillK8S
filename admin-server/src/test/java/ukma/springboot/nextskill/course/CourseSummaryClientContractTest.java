package ukma.springboot.nextskill.course;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import ukma.springboot.nextskill.AdminServerApplication;
import ukma.springboot.nextskill.common.dto.responses.CourseSummaryResponse;
import ukma.springboot.nextskill.config.CourseServiceClientProperties;

import java.util.UUID;

@SpringBootTest(classes = AdminServerApplication.class)
@AutoConfigureStubRunner(
    ids = "ukma.springboot.nextskill:webapp",
    stubsMode = StubRunnerProperties.StubsMode.CLASSPATH
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CourseSummaryClientContractTest {

    private static final UUID COURSE_ID = UUID.fromString("0f13f5ad-0c89-4f4b-905c-8a7b2d893fa0");

    @StubRunnerPort("webapp")
    private int producerPort;

    @Autowired
    private CourseSummaryClient client;

    @Autowired
    private CourseServiceClientProperties properties;

    @BeforeEach
    void pointClientToStubRunner() {
        properties.setBaseUrl("http://localhost:" + producerPort);
    }

    @Test
    void shouldFetchCourseSummaryFromStub() {
        CourseSummaryResponse response = client.getCourseSummary(COURSE_ID);

        Assertions.assertThat(response.getUuid()).isEqualTo(COURSE_ID);
        Assertions.assertThat(response.getName()).isEqualTo("Spring Cloud Fundamentals!!");
        Assertions.assertThat(response.getTeacherFullName()).isEqualTo("Some Wise Man");
        Assertions.assertThat(response.getSectionCount()).isEqualTo(5);
        Assertions.assertThat(response.getEnrolledStudentCount()).isEqualTo(42);
    }
}
