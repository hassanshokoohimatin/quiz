package quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import quiz.model.enums.QuestionType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
//@NoArgsConstructor
@Table
@Entity
@DiscriminatorValue("DetailedQuestion")
public class DetailedQuestion extends Question {

    public DetailedQuestion(){
        super.type = QuestionType.Detailed;
    }

    private String answer;

}
