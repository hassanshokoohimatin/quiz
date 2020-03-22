package quiz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quiz.model.Question;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question , Long> {

    public List<Question> findQuestionsByTitle(String title);

    public List<Question> findQuestionsByTextContaining(String text);

    public List<Question> findQuestionsByCreatedBy_Id(Long id);

}
