package ukma.springboot.nextskill.common.dto.views;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Data
@Getter
@Builder
public class EmailRequest implements Serializable {
    private String to;
    private String subject;
    private String text;
}

