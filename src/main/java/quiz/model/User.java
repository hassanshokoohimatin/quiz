package quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import quiz.model.enums.Gender;
import quiz.model.enums.Status;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Calendar;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Please provide your username")
    private String username;

    @Length(min = 5 , message = "Your password must have at least 5 characters ")
    @NotEmpty(message = "Please provide your password")
    private String password;

    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String nationalCode;

    @Email(message = "Please provide a valid email")
    @NotEmpty(message = "Please provide an email")
    private String email;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP" , insertable = false , updatable = false)
    private Calendar createdDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne
    private Role role;

}
