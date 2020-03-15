package quiz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quiz.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course , Long> {
}
