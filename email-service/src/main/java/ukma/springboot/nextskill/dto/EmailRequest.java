package ukma.springboot.nextskill.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class EmailRequest {
    private String to;
    private String subject;
    private String text;

}

