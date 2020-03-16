package quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import quiz.model.Role;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SearchDto {

    private String firstName;
    private String lastName;
    private String role;

}
