package quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import quiz.model.enums.RoleType;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

}
