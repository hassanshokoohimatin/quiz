package quiz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quiz.model.Course;
import quiz.model.User;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course , Long> {

    public Course findByTitle(String title);

    public List<Course> findCoursesByTeacherId(Long teacherId);

}
