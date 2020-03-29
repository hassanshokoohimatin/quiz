package quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionAnswerDto {

    private Long questionId;

    private Long answerId;

    private String detailedAnswer;

}
