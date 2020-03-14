package quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import quiz.model.enums.Gender;
import quiz.model.enums.Status;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String nationalCode;
    private String email;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne
    private Role role;

}
