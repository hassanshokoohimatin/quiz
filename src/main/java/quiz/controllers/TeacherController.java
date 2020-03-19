package quiz.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import quiz.dto.EditExamDto;
import quiz.model.Course;
import quiz.model.Exam;
import quiz.services.CourseService;
import quiz.services.ExamService;
import quiz.services.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExamService examService;

    @RequestMapping(value = "/backToMainMenuOfTeacher")
    public String back(Model model , @RequestParam("teacherId") Long teacherId){

        model.addAttribute("teacherId" , teacherId);
        return "teacher";
    }

    @RequestMapping(value = "/listTeacherCourses")
    public String listTeacherCourses(Model model , @RequestParam("teacherId") Long teacherId){

        List<Course> courses = courseService.findAllCoursesByTeacherId(teacherId);

        model.addAttribute("courses" , courses);
        model.addAttribute("teacherId" , teacherId);

        return "list-teacher-courses";

    }

    @RequestMapping(value = "/addExamToCourse")
    public String addExamToCourse(Model model , @RequestParam("courseId") Long courseId){

        Exam exam = new Exam();

        model.addAttribute("exam" , exam);
        model.addAttribute("courseId" , courseId);

        return "add-exam-to-course";
    }

    @RequestMapping(value = "/addExam" , method = RequestMethod.POST)
    public String addExam(Model model , @ModelAttribute Exam exam , @RequestParam("courseId") Long courseId){


        Exam newExam = new Exam();

        newExam.setName(exam.getName());
        newExam.setDescription(exam.getDescription());
        newExam.setCreatedDate(exam.getCreatedDate());
        newExam.setCourse(courseService.findCourseById(courseId));
        newExam.setCreatedBy(userService.findById(courseService.findCourseById(courseId).getTeacher().getId()));
        newExam.setPublished(false);
        newExam.setQuestions(null);

        examService.saveExam(newExam);

        model.addAttribute("courses" , courseService.findAllCoursesByTeacherId(courseService.findCourseById(courseId).getTeacher().getId()));
        model.addAttribute("teacherId" , courseService.findCourseById(courseId).getTeacher().getId());

        return "list-teacher-courses";
    }

    @RequestMapping(value = "/listExamsOfCourse")
    public String listExamsOfCourse(Model model , @RequestParam("courseId") Long courseId){

        List<Exam> exams = examService.findExamsByCourseId(courseId);

        model.addAttribute("exams" , exams);
        model.addAttribute("courseId" , courseId);

        return "list-exams-of-course";
    }

    @RequestMapping(value = "/editExam/{examId}")
    public String editExam(Model model ,
                           @ModelAttribute ArrayList<Exam> exams ,
                           @PathVariable(value = "examId") Long examId){

        EditExamDto editExamDto = new EditExamDto();

        model.addAttribute("editExamDto" , editExamDto);
        model.addAttribute("examId" , examId);
        model.addAttribute("examName" , examService.findExamById(examId).getName());
        model.addAttribute("examDescription" , examService.findExamById(examId).getDescription());
        model.addAttribute("exams" , exams);

        return "edit-exam";
    }

    @RequestMapping(value = "/submitEditExam/{examId}")
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

    @RequestMapping(value = "/delete/{examId}/{courseId}")
    public String delete(Model model ,
                         @PathVariable(value = "examId") Long examId ,
                         @PathVariable(value = "courseId") Long courseId){

        examService.removeExamById(examId);

        model.addAttribute("courseId" , courseId);
        model.addAttribute("exams" , examService.findExamsByCourseId(courseId));

        return "list-exams-of-course";
    }

}




