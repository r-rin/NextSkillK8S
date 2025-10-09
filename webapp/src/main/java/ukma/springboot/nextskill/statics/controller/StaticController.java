package ukma.springboot.nextskill.statics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ukma.springboot.nextskill.statics.service.StaticService;

@RestController
@RequestMapping("static")
@RequiredArgsConstructor
public class StaticController {

    private final StaticService staticService;

    @GetMapping(value = "image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Object> getImage() {
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(staticService.getImage());
    }

    @GetMapping(value = "music", produces = "audio/mpeg")
    public ResponseEntity<Object> getMusic() {
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("audio/mpeg"))
            .body(staticService.getMusic());
    }

    @GetMapping(value = "interestingVideo", produces = "video/mp4")
    public ResponseEntity<Object> getInterestingVideo() {
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("video/mp4"))
            .body(staticService.getInterestingVideo());
    }

    @GetMapping(value = "json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getJson() {
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(staticService.getJson());
    }
}
