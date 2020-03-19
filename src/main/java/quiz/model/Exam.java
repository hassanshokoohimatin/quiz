package quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
@Table
@Entity
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 2 , max = 15 , message = "The name must be between 2 and 15 characters")
    @NotNull(message = "Please provide a name")
    private String name;

    @Size(max = 500 , message = "The description can't be longer than 500 characters")
    @NotNull(message = "Please provide a description")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdDate;

    private boolean isPublished = false;

    @OneToOne
    private User createdBy;

    @ManyToOne
    private Course course;

    @ManyToMany
    private List<Question> questions;

}
