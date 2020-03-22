package quiz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quiz.model.Exam;
import quiz.model.Question;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam , Long> {

    public List<Exam> findExamsByCourse_Id(Long id);

}
