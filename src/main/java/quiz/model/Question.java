package quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import quiz.model.enums.QuestionType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 5 , max = 200 , message = "The question should be between 5 and 200 characters")
    @NotNull(message = "Question text not provided")
    private String text;

    @NotNull(message = "Question title can not be empty")
    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdDate;

    private float defaultScore;

    @Enumerated(EnumType.STRING)
    protected QuestionType type;

    @ManyToOne
    private User createdBy;

}
