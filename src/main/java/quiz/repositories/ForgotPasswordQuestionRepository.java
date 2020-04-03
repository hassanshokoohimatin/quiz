package quiz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quiz.model.ForgotPasswordQuestion;

@Repository
public interface ForgotPasswordQuestionRepository extends JpaRepository<ForgotPasswordQuestion , Long> {
}
