package ukma.springboot.nextskill.contract;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ukma.springboot.nextskill.common.dto.responses.CourseSummaryResponse;
import ukma.springboot.nextskill.course.CourseService;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public abstract class CourseSummaryContractBase {

    protected static final UUID COURSE_ID = UUID.fromString("0f13f5ad-0c89-4f4b-905c-8a7b2d893fa0");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        Mockito.when(courseService.getSummary(COURSE_ID)).thenReturn(
                CourseSummaryResponse.builder()
                        .uuid(COURSE_ID)
                        .name("Spring Cloud Fundamentals!!")
                        .teacherFullName("Some Wise Man")
                        .sectionCount(5)
                        .enrolledStudentCount(42)
                        .build()
        );
    }
}
