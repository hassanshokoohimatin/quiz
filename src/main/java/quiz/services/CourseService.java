package quiz.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.model.Course;
import quiz.model.User;
import quiz.repositories.CourseRepository;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> findAllCourses(){
        return courseRepository.findAll();
    }

    public Course findCourseById(Long id){
        return courseRepository.findById(id).get();
    }

    public Course findCourseByTitle(String title){
        return courseRepository.findByTitle(title);
    }

    public void saveCourse(Course course){
        courseRepository.save(course);
    }

}
