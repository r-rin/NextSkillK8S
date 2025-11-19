package ukma.springboot.nextskill.course;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ukma.springboot.nextskill.common.dto.responses.CourseSummaryResponse;
import ukma.springboot.nextskill.config.CourseServiceClientProperties;

import java.time.Duration;
import java.util.UUID;

@Component
public class CourseSummaryClient {

    private final RestTemplateBuilder restTemplateBuilder;
    private final CourseServiceClientProperties properties;

    public CourseSummaryClient(RestTemplateBuilder restTemplateBuilder, CourseServiceClientProperties properties) {
        this.restTemplateBuilder = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(5));
        this.properties = properties;
    }

    public CourseSummaryResponse getCourseSummary(UUID courseUuid) {
        ResponseEntity<CourseSummaryResponse> response = restTemplateBuilder.build()
                .getForEntity(buildSummaryUrl(courseUuid), CourseSummaryResponse.class);

        CourseSummaryResponse body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("Course summary response did not contain a body");
        }
        return body;
    }

    private String buildSummaryUrl(UUID courseUuid) {
        String sanitizedBaseUrl = properties.getBaseUrl().endsWith("/")
                ? properties.getBaseUrl().substring(0, properties.getBaseUrl().length() - 1)
                : properties.getBaseUrl();

        return UriComponentsBuilder
                .fromHttpUrl(sanitizedBaseUrl)
                .path("/api/courses/{courseUuid}/summary")
                .buildAndExpand(courseUuid)
                .toUriString();
    }
}
