package ukma.springboot.nextskill.statics.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StaticControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void getImage_shouldReturnImageJpegMimeType() throws Exception {
        mockMvc.perform(get("/static/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    @WithMockUser
    void getMusic_shouldReturnAudioMpegMimeType() throws Exception {
        mockMvc.perform(get("/static/music"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("audio/mpeg")));
    }

    @Test
    @WithMockUser
    void getInterestingVideo_shouldReturnVideoMp4MimeType() throws Exception {
        mockMvc.perform(get("/static/interestingVideo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("video/mp4")));
    }

    @Test
    @WithMockUser
    void getJson_shouldReturnApplicationJsonMimeType() throws Exception {
        mockMvc.perform(get("/static/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    void getImage_shouldReturnImageJpegInProducesHeader() throws Exception {
        mockMvc.perform(get("/static/image"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_JPEG_VALUE));
    }

    @Test
    @WithMockUser
    void getMusic_shouldReturnAudioMpegInProducesHeader() throws Exception {
        mockMvc.perform(get("/static/music"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "audio/mpeg"));
    }

    @Test
    @WithMockUser
    void getInterestingVideo_shouldReturnVideoMp4InProducesHeader() throws Exception {
        mockMvc.perform(get("/static/interestingVideo"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "video/mp4"));
    }

    @Test
    @WithMockUser
    void getJson_shouldReturnJsonInProducesHeader() throws Exception {
        mockMvc.perform(get("/static/json"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE));
    }
}
