package ukma.springboot.nextskill.common.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSummaryResponse {
    private UUID uuid;
    private String name;
    private String teacherFullName;
    private int sectionCount;
    private int enrolledStudentCount;
}
