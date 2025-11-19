package ukma.springboot.nextskill.course.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ukma.springboot.nextskill.common.dto.responses.CourseSummaryResponse;
import ukma.springboot.nextskill.course.CourseService;

import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseApiController {

    private final CourseService courseService;

    @GetMapping("/{courseUuid}/summary")
    public ResponseEntity<CourseSummaryResponse> getCourseSummary(@PathVariable UUID courseUuid) {
        return ResponseEntity.ok(courseService.getSummary(courseUuid));
    }
}
