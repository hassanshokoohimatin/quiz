package quiz.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import quiz.model.Course;
import quiz.services.CourseService;
import quiz.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/student")
public class StudentController {

    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;

    @RequestMapping(value = "/listStudentCourses/{studentId}")
    public String listStudentCourses(Model model ,
                                     @PathVariable(value = "studentId") Long studentId){

        List<Course> studentCourses = courseService.findAllCourses().stream()
                .filter(course -> course.getStudents().contains(userService.findById(studentId)))
                .collect(Collectors.toList());

        model.addAttribute("studentCourses" , studentCourses);

        return "list-student-courses";
    }
}
