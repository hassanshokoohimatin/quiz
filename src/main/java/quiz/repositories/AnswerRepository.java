package quiz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quiz.model.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer , Long> {
}
