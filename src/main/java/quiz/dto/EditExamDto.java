package quiz.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EditExamDto {

    private String name;
    private String description;
    private Integer time;

}
