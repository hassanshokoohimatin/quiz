package quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor @NoArgsConstructor
@Table
@Entity
@DiscriminatorValue("MultiChoiceQuestion")
public class MultiChoiceQuestion extends Question {

    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    private List<Answer> answers;

    @OneToOne
    private Answer correctAnswer;

}