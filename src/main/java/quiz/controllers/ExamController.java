package quiz.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import quiz.dto.EditExamDto;
import quiz.model.Course;
import quiz.model.Exam;
import quiz.services.CourseService;
import quiz.services.ExamService;

import java.util.ArrayList;
import java.util.List;//TODO:solve confirm problem in template list-exams-of-course

@Controller
@RequestMapping(value = "/exam")
public class ExamController {

    @Autowired
    private ExamService examService;
    @Autowired
    private CourseService courseService;

    @RequestMapping(value = "/editExam/{examId}")
    public String editExam(Model model ,
                           @ModelAttribute EditExamDto editExamDto ,
                           @PathVariable(value = "examId") Long examId){

        Exam exam = examService.findExamById(examId);

        exam.setName(editExamDto.getName());
        exam.setDescription(editExamDto.getDescription());

        examService.saveExam(exam);

        Long teacherId = examService.findExamById(examId).getCreatedBy().getId();
        List<Course> courses = courseService.findAllCoursesByTeacherId(teacherId);

        model.addAttribute("courses" , courses);
        model.addAttribute("teacherId" , teacherId);

        return "list-teacher-courses";
    }

    @RequestMapping(value = "/delete/{examId}")
    public String delete(@PathVariable Long examId){

        examService.removeExamById(examId);

        return "index";
    }

}
