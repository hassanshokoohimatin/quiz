package quiz.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import quiz.dto.EditExamDto;
import quiz.dto.ScoreDto;
import quiz.model.*;
import quiz.model.enums.CorrectionStatus;
import quiz.services.*;

import javax.sound.midi.SoundbankResource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private static Long signedInTeacherId = 0L;

    @Autowired
    private CourseService courseService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExamService examService;
    @Autowired
    private ExamPaperService examPaperService;
    @Autowired
    private QuestionService questionService;

    @RequestMapping(value = "/backToMainMenuOfTeacher")
    public String back(Model model , @RequestParam("teacherId") Long teacherId){

        model.addAttribute("teacherId" , teacherId);
        return "teacher";
    }

    @RequestMapping(value = "/listTeacherCourses/{teacherId}")
    public String listTeacherCourses(Model model , @PathVariable("teacherId") Long teacherId){

        List<Course> courses = courseService.findAllCoursesByTeacherId(teacherId);

        model.addAttribute("courses" , courses);
        model.addAttribute("teacherId" , teacherId);

        signedInTeacherId = teacherId ;

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
        newExam.setTime(exam.getTime());
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

        Map<Exam , Float> examTotalScore = new HashMap<>();
        for (Exam exam : exams){
            float totalScore = 0;
            for (Question question : exam.getQuestions()){
                totalScore += question.getDefaultScore();
            }
            examTotalScore.put(exam , totalScore);
        }

        model.addAttribute("examTotalScoreMap" , examTotalScore);
        model.addAttribute("courseId" , courseId);
        model.addAttribute("exams" , exams);

        return "list-exams-of-course";
    }

    @RequestMapping(value = "/editExam/{examId}")
    public String editExam(Model model ,
                           @ModelAttribute ArrayList<Exam> exams ,
                           @PathVariable(value = "examId") Long examId){

        EditExamDto editExamDto = new EditExamDto();

        Exam exam = examService.findExamById(examId);
        float examTotalScore = 0;
        for (Question question : exam.getQuestions()){
            examTotalScore += question.getDefaultScore();
        }

        model.addAttribute("editExamDto" , editExamDto);
        model.addAttribute("exam" , exam);
        model.addAttribute("examTotalScore" , examTotalScore);
        model.addAttribute("exams" , exams);

        return "edit-exam";
    }

    @RequestMapping(value = "/submitEditExam/{examId}")
    public String editExam(Model model ,
                           @ModelAttribute EditExamDto editExamDto ,
                           @PathVariable(value = "examId") Long examId){

        Exam exam = examService.findExamById(examId);

        if (2 <= editExamDto.getName().length() && editExamDto.getName().length() <= 15)
            exam.setName(editExamDto.getName());
        if (editExamDto.getDescription().length() != 0)
            exam.setDescription(editExamDto.getDescription());
        if (editExamDto.getTime() != null)
            exam.setTime(editExamDto.getTime());
        if (editExamDto.getPassingScore() != 0)
            exam.setPassingScore(editExamDto.getPassingScore());

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

    @RequestMapping(value = "/submission/{examId}/{courseId}")
    public String submission(Model model ,
                             @PathVariable(value = "examId") Long examId ,
                             @PathVariable(value = "courseId") Long courseId){

        Exam exam = examService.findExamById(examId);
        List<Exam> exams = examService.findExamsByCourseId(courseId);

        if (exam.isPublished() == false){
            exam.setPublished(true);
            examService.saveExam(exam);
            model.addAttribute("exams" , exams);
            model.addAttribute("courseId" , courseId);
            return "list-exams-of-course";
        }
        else {
            exam.setPublished(false);
            examService.saveExam(exam);
            model.addAttribute("exams" , exams);
            model.addAttribute("courseId" , courseId);
            return "list-exams-of-course";
        }
    }

    @RequestMapping(value = "/correction/{teacherId}")
    public String correction(Model model ,
                             @PathVariable("teacherId") Long teacherId){

        List<Course> teacherCourses = courseService.findAllCoursesByTeacherId(teacherId);

        model.addAttribute("teacherCourses" , teacherCourses);

        return "list-courses-to-exam-correction";
    }

    @RequestMapping("/listExamsToCorrection/{courseId}")
    public String listExamsToCorrection(Model model ,
                                        @PathVariable("courseId") Long courseId){

        List<Exam> exams = examService.findExamsByCourseId(courseId);

        Map<Exam , Integer> examNotCorrectedMap = new HashMap<>();

        for (Exam exam : exams){
            Integer numberOfNotCorrectedPapers = 0;
            for (User student : exam.getContributors()){
                ExamPaper examPaper = examPaperService.findExamPaperOfAnStudentInOneExam(exam.getId() , student.getId());
                if (examPaper.getCorrection().equals(CorrectionStatus.NotCorrected))
                    numberOfNotCorrectedPapers += 1;
            }
            examNotCorrectedMap.put(exam , numberOfNotCorrectedPapers);
        }

        model.addAttribute("exams" , examNotCorrectedMap);

        return "list-exams-to-correction";
    }

    @RequestMapping(value = "/showExamPapers/{examId}")
    public String showExamPapers(Model model ,
                                 @PathVariable("examId") Long examId){

        Map<ExamPaper , String> examPaperNameMap = new HashMap<>();
        List<User> students = examService.findExamById(examId).getContributors();

        for (User student : students){
            ExamPaper examPaper = examPaperService.findExamPaperOfAnStudentInOneExam(examId , student.getId());
            String name = student.getFirstName() + "  " + student.getLastName();
            examPaperNameMap.put(examPaper , name);
        }

        model.addAttribute("examPaperNameMap" , examPaperNameMap);

        return "list-examPapers-to-correction";
    }

    @RequestMapping(value = "/correctExamPaper/{examPaperId}")
    public String correctExamPaper(Model model ,
                                   @PathVariable(value = "examPaperId") Long examPaperId){

        ExamPaper examPaper = examPaperService.findById(examPaperId);

        //==============================================================================================================
        //this is for reCorrection
        if (examPaper.getCorrection().equals(CorrectionStatus.Corrected)){
            float detailedQuestionsScoresByTeacher = 0;
            for (float score : examPaper.getDetailedQuestionsScoresByTeacher().values()){
                detailedQuestionsScoresByTeacher += score;
            }
            examPaper.setStudentScore(examPaper.getStudentScore() - detailedQuestionsScoresByTeacher);
            examPaperService.save(examPaper);
        }
        //==============================================================================================================

        Exam exam = examService.findExamById(examPaper.getExamId());
        List<Question> examQuestions = exam.getQuestions();
        List<DetailedQuestion> examDetailedQuestions = examQuestions.stream().filter(question -> question instanceof DetailedQuestion).
                map(question -> (DetailedQuestion) question).collect(Collectors.toList());

        DetailedQuestion question = examDetailedQuestions.get(0);

        ScoreDto scoreDto = new ScoreDto();

        model.addAttribute("question" , question);
        model.addAttribute("questionCounter" , 0);
        model.addAttribute("scoreDto" , scoreDto);
        model.addAttribute("examPaper" , examPaper);

        return "correct-examPaper";

    }

    @RequestMapping("/submitScore/{examPaperId}/{questionId}/{questionCounter}")
    public String submitScore(Model model , @ModelAttribute("scoreDto") ScoreDto scoreDto ,
                              @PathVariable("examPaperId") Long examPaperId ,
                              @PathVariable("questionId") Long questionId ,
                              @PathVariable("questionCounter") int questionCounter){

        ExamPaper examPaper = examPaperService.findById(examPaperId);
        DetailedQuestion detailedQuestion = (DetailedQuestion) questionService.findQuestionById(questionId);
        examPaper.getDetailedQuestionsScoresByTeacher().put(questionId , scoreDto.getScore());

        examPaper.setStudentScore(examPaper.getStudentScore() +
                examPaper.getDetailedQuestionsScoresByTeacher().get(questionId));

        examPaperService.save(examPaper);



        Exam exam = examService.findExamById(examPaper.getExamId());
        List<Question> examQuestions = exam.getQuestions();
        List<DetailedQuestion> examDetailedQuestions = examQuestions.stream().filter(question -> question instanceof DetailedQuestion).
                map(question -> (DetailedQuestion) question).collect(Collectors.toList());

        if (questionCounter == examDetailedQuestions.size()){

            examPaper.setCorrection(CorrectionStatus.Corrected);
            examPaperService.save(examPaper);
            return "finish-correction";
        }
        else {

            DetailedQuestion question = examDetailedQuestions.get(questionCounter);

            model.addAttribute("question" , question);
            model.addAttribute("examPaper" , examPaper);
            model.addAttribute("questionCounter" , questionCounter);
            model.addAttribute("scoreDto" , scoreDto);

            return "correct-examPaper";
        }
    }

}




