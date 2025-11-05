package ukma.springboot.nextskill.common.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ukma.springboot.nextskill.common.dto.enums.UserRole;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {
    private UUID uuid;
    private String username;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String description;
    private LocalDateTime createdAt;
    private UserRole role;
    private boolean isDisabled;
    private List<CourseResponse> ownCourses;
    private List<CourseResponse> courses;
}
