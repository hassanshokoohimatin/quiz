package quiz.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import quiz.dto.EditAccountDto;
import quiz.dto.QuestionAnswerDto;
import quiz.model.*;
import quiz.model.enums.CorrectionStatus;
import quiz.model.enums.ExamScoringType;
import quiz.services.*;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/student")
public class StudentController {

    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ExamService examService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ExamPaperService examPaperService;

    @RequestMapping(value = "/backToStudent/{studentId}")
    public String back(@PathVariable("studentId") Long studentId){
        return "student";
    }

    @RequestMapping("/signOut")
    public String signOut(){
        return "index";
    }

    @RequestMapping(value = "/listStudentCourses/{studentId}")
    public String listStudentCourses(Model model ,
                                     @PathVariable(value = "studentId") Long studentId){

        List<Course> studentCourses = courseService.findAllCourses().stream()
                .filter(course -> course.getStudents().contains(userService.findById(studentId)))
                .collect(Collectors.toList());

        model.addAttribute("studentCourses" , studentCourses);

        return "list-student-courses";
    }

    @RequestMapping(value = "/examsOfCourse/{courseId}/{studentId}")
    public String examsOfCourse(Model model ,
                                @PathVariable(value = "courseId") Long courseId ,
                                @PathVariable(value = "studentId") Long studentId){

        List<Exam> courseExams = courseService.findCourseById(courseId).getExams().stream()
                .filter(exam -> exam.isPublished()==true)
                .collect(Collectors.toList());

        model.addAttribute("courseExams" , courseExams);

        return "list-course-exams";
    }

    @RequestMapping(value = "/gotThisExam/{examId}/{studentId}")
    public String gotThisExam(Model model ,
                              @PathVariable(value = "examId") Long examId ,
                              @PathVariable(value = "studentId") Long studentId){

        if (examService.findExamById(examId).getContributors().contains(userService.findById(studentId))){

            model.addAttribute("courseId" , examService.findExamById(examId).getCourse().getId());
            model.addAttribute("studentId" , studentId);

            return "no-permitted-exam";
        }else {

            examService.findExamById(examId).getContributors().add(userService.findById(studentId));
            examService.saveExam(examService.findExamById(examId));

            ExamPaper studentExamPaper = new ExamPaper();
            studentExamPaper.setExamId(examId);
            studentExamPaper.setStudentId(studentId);
            studentExamPaper.setExamDate(new Date());
            studentExamPaper.setStudentScore(0);
            studentExamPaper.setCorrection(CorrectionStatus.NotCorrected);

            examPaperService.save(studentExamPaper);

            List<Question> examQuestions = examService.findExamById(examId).getQuestions();

            int detailedQuestionCounter = 0;
            for (Question question : examQuestions){
                if (question instanceof DetailedQuestion) {
                    detailedQuestionCounter = detailedQuestionCounter + 1;
                    break;
                }
            }

            if (detailedQuestionCounter != 0)
                examService.findExamById(examId).setScoringType(ExamScoringType.Manual);
            else
                examService.findExamById(examId).setScoringType(ExamScoringType.Automatic);

            examService.saveExam(examService.findExamById(examId));

            QuestionAnswerDto questionAnswerDto = new QuestionAnswerDto();

            Question question = examQuestions.get(0);
            questionAnswerDto.setQuestionId(question.getId());

            if (question instanceof MultiChoiceQuestion)
                model.addAttribute("question", (MultiChoiceQuestion) question);
            else
                model.addAttribute("question" , (DetailedQuestion) question);
            //==========================================================================================================
            Integer examTimeMinutes = examService.findExamById(examId).getTime();
            Date examFinishTime = new Date();
            if (examTimeMinutes/60 > 0){
                int examHour = examTimeMinutes/60;
                int examMin = examTimeMinutes%60;
                examFinishTime.setHours(examFinishTime.getHours() + examHour);
                examFinishTime.setMinutes(examFinishTime.getMinutes() + examMin);
            }else {
                examFinishTime.setMinutes(examFinishTime.getMinutes() + examTimeMinutes);
            }
            //==========================================================================================================
            model.addAttribute("questionAnswerDto" , questionAnswerDto);
            model.addAttribute("questionCounter" , 0);
            model.addAttribute("size" , examQuestions.size());
            model.addAttribute("examFinishTime" , examFinishTime.getTime());

            return "exam-start";
        }
    }

    @RequestMapping(value = "/submitQuestion/{examId}/{studentId}/{questionCounter}/{examFinishTime}" , method = RequestMethod.POST)
    public String submitExam(Model model ,
                             @PathVariable("examId") Long examId ,
                             @PathVariable("studentId") Long studentId ,
                             @PathVariable("questionCounter") int questionCounter ,
                             @ModelAttribute("questionAnswerDto") QuestionAnswerDto questionAnswerDto ,
                             @PathVariable("examFinishTime") Long examFinishTime){

        Question submittedQuestion = questionService.findQuestionById(questionAnswerDto.getQuestionId());

        ExamPaper studentExamPaper = examPaperService.findExamPaperOfAnStudentInOneExam(examId , studentId);

        if (submittedQuestion instanceof MultiChoiceQuestion){
            if (questionAnswerDto.getAnswerId() != null) {

                if (studentExamPaper.getMlcQuestionsAnswers().keySet().contains(questionAnswerDto.getQuestionId())){

                    Answer oldAnswer = answerService.findAnswerById(studentExamPaper.getMlcQuestionsAnswers().get(questionAnswerDto.getQuestionId()));
                    MultiChoiceQuestion question = (MultiChoiceQuestion) questionService.findQuestionById(questionAnswerDto.getQuestionId());
                    if (oldAnswer.equals(question.getCorrectAnswer())){
                        float studentScore = studentExamPaper.getStudentScore();
                        studentExamPaper.setStudentScore(studentScore - question.getDefaultScore());
                        examPaperService.save(studentExamPaper);
                    }

                    studentExamPaper.getMlcQuestionsAnswers().replace(questionAnswerDto.getQuestionId() , questionAnswerDto.getAnswerId());
                    examPaperService.save(studentExamPaper);
                    if (question.getCorrectAnswer().equals(answerService.findAnswerById(questionAnswerDto.getAnswerId()))) {
                        float studentScore = studentExamPaper.getStudentScore();
                        studentExamPaper.setStudentScore(studentScore + question.getDefaultScore());
                        examPaperService.save(studentExamPaper);
                    }
                }
                else {

                    studentExamPaper.getMlcQuestionsAnswers().put(questionAnswerDto.getQuestionId(), questionAnswerDto.getAnswerId());
                    examService.saveExam(examService.findExamById(examId));
                    if (((MultiChoiceQuestion) submittedQuestion).getCorrectAnswer().equals(answerService.findAnswerById(questionAnswerDto.getAnswerId()))) {
                        float studentScore = studentExamPaper.getStudentScore();
                        studentExamPaper.setStudentScore(studentScore + submittedQuestion.getDefaultScore());
                        examPaperService.save(studentExamPaper);
                    }
                }
            }
        }
        else {
            studentExamPaper.getDetailedQuestionsAnswers().put(questionAnswerDto.getQuestionId() , questionAnswerDto.getDetailedAnswer());
            examPaperService.save(studentExamPaper);
        }

        List<Question> examQuestions = examService.findExamById(examId).getQuestions();

        //==============================================================================================================
        if (questionCounter == examQuestions.size()) {

            studentExamPaper.setPassingScore(examService.findExamById(examId).getPassingScore());

            float totalScore = 0;
            for (Question question : examQuestions)
                totalScore += question.getDefaultScore();
            studentExamPaper.setTotalScore(totalScore);
            examPaperService.save(studentExamPaper);

            for (Question q : examService.findExamById(examId).getQuestions()){
                if (q instanceof DetailedQuestion) {
                    if (!studentExamPaper.getDetailedQuestionsAnswers().keySet().contains(q.getId()))
                        studentExamPaper.getDetailedQuestionsAnswers().put(q.getId() , "");
                }
            }

            examPaperService.save(studentExamPaper);

            if (examService.findExamById(examId).getScoringType().equals(ExamScoringType.Automatic)){

                int numberOfExamQuestions = examQuestions.size();
                int numberOfAnsweredQuestions = studentExamPaper.getMlcQuestionsAnswers().size();
                int numberOfCorrectAnswered = 0;
                for (Long questionId : studentExamPaper.getMlcQuestionsAnswers().keySet()){
                    MultiChoiceQuestion question = (MultiChoiceQuestion) questionService.findQuestionById(questionId);
                    Answer correctAnswer = question.getCorrectAnswer();
                    Answer studentAnswer = answerService.findAnswerById(studentExamPaper.getMlcQuestionsAnswers().get(questionId));
                    if (correctAnswer.equals(studentAnswer))
                        numberOfCorrectAnswered += 1;
                }

                studentExamPaper.setCorrection(CorrectionStatus.Corrected);
                examPaperService.save(studentExamPaper);

                model.addAttribute("numberOfExamQuestions" , numberOfExamQuestions);
                model.addAttribute("numberOfAnsweredQuestions" , numberOfAnsweredQuestions);
                model.addAttribute("numberOfCorrectAnswered" , numberOfCorrectAnswered);
                model.addAttribute("studentExamPaper" , studentExamPaper);

                return "exam-finish-automatic-scoring";
            }
            else {
                model.addAttribute("studentId" , studentId);
                return "exam-finish-manual-scoring";
            }

        }//=============================================================================================================
        else {
            Question question = examQuestions.get(questionCounter);
            questionAnswerDto.setQuestionId(question.getId());

            if (question instanceof MultiChoiceQuestion)
                model.addAttribute("question", (MultiChoiceQuestion) question);
            else
                model.addAttribute("question", (DetailedQuestion) question);

            model.addAttribute("questionAnswerDto", questionAnswerDto);
            model.addAttribute("questionCounter", questionCounter);
            model.addAttribute("size" , examQuestions.size());
            model.addAttribute("examFinishTime" , examFinishTime);

            return "exam-start";
        }
    }

    @RequestMapping("/examsResults/{studentId}")
    public String results(Model model , @PathVariable("studentId") Long studentId){

        List<ExamPaper> studentExamPapers = examPaperService.findExamPapersOfAnStudent(studentId);

        model.addAttribute("studentExamPapers" , studentExamPapers);
        model.addAttribute("examService" , examService);

        return "exams-results-to-student";
    }

    @RequestMapping("/showExamPaperToStudent/{examPaperId}")
    public String showExamPaper(Model model , @PathVariable("examPaperId") Long examPaperId){

        ExamPaper studentExamPaper = examPaperService.findById(examPaperId);

        model.addAttribute("studentExamPaper" , studentExamPaper);
        model.addAttribute("examService" , examService);

        return "student-examPaper";
    }

}
