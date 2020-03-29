package quiz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quiz.model.ExamPaper;

import java.util.List;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaper , Long> {

    public ExamPaper findExamPaperByExamIdAndStudentId(Long examId , Long studentId);

    public List<ExamPaper> findExamPapersByStudentId(Long studentId);
}
