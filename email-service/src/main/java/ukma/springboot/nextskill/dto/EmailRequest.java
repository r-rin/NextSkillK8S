package ukma.springboot.nextskill.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class EmailRequest {
    private String to;
    private String subject;
    private String text;

}

