package quiz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import quiz.model.enums.CorrectionStatus;

import javax.persistence.*;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class ExamPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long examId;

    private Long studentId;

    private float totalScore;

    private float studentScore;

    private float passingScore;

    @Enumerated(EnumType.STRING)
    private CorrectionStatus correction;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date examDate;

    @ElementCollection
    private Map<Long , Long> mlcQuestionsAnswers = new HashMap<>();

    @ElementCollection
    private Map<Long , String> detailedQuestionsAnswers = new HashMap<>();

    @ElementCollection
    private Map<Long , Float> detailedQuestionsScoresByTeacher = new HashMap<>();

}
