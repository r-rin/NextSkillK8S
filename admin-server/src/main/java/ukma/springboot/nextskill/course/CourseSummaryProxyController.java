package ukma.springboot.nextskill.course;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ukma.springboot.nextskill.common.dto.responses.CourseSummaryResponse;

import java.util.UUID;

@RestController
@RequestMapping("/admin/courses")
@RequiredArgsConstructor
public class CourseSummaryProxyController {

    private final CourseSummaryClient courseSummaryClient;

    @GetMapping("/{courseUuid}/summary")
    public ResponseEntity<CourseSummaryResponse> getCourseSummary(@PathVariable UUID courseUuid) {
        return ResponseEntity.ok(courseSummaryClient.getCourseSummary(courseUuid));
    }
}
