package ukma.springboot.nextskill.statics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class StaticService {

    private final RestTemplate restTemplate;

    public Object getImage() {
        return new ClassPathResource("static/images/deault_course.jpeg");
    }

    public Object getMusic() {
        return new ClassPathResource("static/audio/music.mp3");
    }

    public Object getInterestingVideo() {
        return new ClassPathResource("static/video/interesting_video.mp4");
    }

    @Retryable(
        recover = "getJsonRecover",
        retryFor = { RestClientException.class },
        backoff = @Backoff(delay = 1200, multiplier = 2)
    )
    public Object getJson() {
        return restTemplate.getForObject("https://www.reddit.com/r/ios/top.json", Object.class);
    }

    @Recover
    public Object getJsonRecover(Exception e) {
        System.out.println("Recovering from Exception: " + e.getMessage());
        return new ClassPathResource("static/json/example.json");
    }
}
