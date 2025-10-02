package ukma.springboot.nextskill.common.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "questions")
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID id;

    @Column(nullable = false)
    private String questionText;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private TestEntity test;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<QuestionOptionEntity> questionOptions;
}
