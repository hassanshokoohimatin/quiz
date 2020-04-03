package quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EditAccountDto {

    private String username;
    private String OldPassword;
    private String newPassword;
    private String newPasswordConfirm;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;

}
