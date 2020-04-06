package quiz.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import quiz.dto.QuestionTypeDto;
import quiz.model.Answer;
import quiz.model.DetailedQuestion;
import quiz.model.MultiChoiceQuestion;
import quiz.model.Question;
import quiz.model.enums.QuestionType;
import quiz.services.AnswerService;
import quiz.services.ExamService;
import quiz.services.QuestionService;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private ExamService examService;
    @Autowired
    private QuestionService qService;
    @Autowired
    private AnswerService answerService;

    @RequestMapping(value = "/questions/{examId}")
    public String questions(Model model ,
                            @PathVariable ("examId") Long examId){

        List<Question> questions = examService.findExamById(examId).getQuestions();

        model.addAttribute("questions" , questions);
        model.addAttribute("examId" , examId);
        model.addAttribute("examService" , examService);

        return "list-questions-of-exam";
    }

    @RequestMapping("/editQuestion/{questionId}/{examId}")
    public String editQuestion(@PathVariable(value = "questionId") Long questionId ,
                               @PathVariable(value = "examId") Long examId){

        return "";//TODO:edit question//this question must edited only in this exam not in the others

    }

    @RequestMapping("/deleteQuestion/{questionId}/{examId}")
    public String deleteQuestion(Model model ,
                                 @PathVariable(value = "questionId") Long questionId ,
                                 @PathVariable(value = "examId") Long examId){


        examService.findExamById(examId).getQuestions().remove(qService.findQuestionById(questionId));
        examService.saveExam(examService.findExamById(examId));

        model.addAttribute("questions" , examService.findExamById(examId).getQuestions());
        model.addAttribute("examId" , examId);

        return "list-questions-of-exam";
    }

    @RequestMapping(value = "/addQuestion/{examId}")
    public String addQuestion(Model model ,
                              @PathVariable(value = "examId") Long examId){

        Long teacherId = examService.findExamById(examId).getCourse().getTeacher().getId();

        List<Question> questions = qService.findQuestionsByTeacherId(teacherId);//question bank of this teacher

        List<Question> questionList = new ArrayList<>();//list of questions of question bank that not exist in the exam

        List<String> allQuestionTypes = new ArrayList<>();

        QuestionTypeDto questionTypeDto = new QuestionTypeDto();

        for (QuestionType q : QuestionType.values()){
            allQuestionTypes.add(q.toString());
        }

        for (Question question : questions){
            if (!examService.findExamById(examId).getQuestions().contains(question)){
                questionList.add(question);
            }
        }

        model.addAttribute("questions" , questionList);
        model.addAttribute("examId" , examId);
        model.addAttribute("questionTypes" , allQuestionTypes);
        model.addAttribute("questionTypeDto" , questionTypeDto);

        return "list-questions-to-add-to-exam";
    }

    @RequestMapping(value = "/addSelectedQuestionToExam/{questionId}/{examId}")
    public String addQuestionToExam(Model model ,
                                    @PathVariable(value = "questionId") Long questionId ,
                                    @PathVariable(value = "examId") Long examId){
        examService.findExamById(examId).getQuestions().add(qService.findQuestionById(questionId));
        examService.saveExam(examService.findExamById(examId));

        return "redirect:/question/addQuestion/{examId}";
    }

    @RequestMapping(value = "/newQuestion/{examId}")
    public String newQuestion(Model model ,
                              @PathVariable(value = "examId") Long examId ,
                              @ModelAttribute("questionTypeDto") QuestionTypeDto questionTypeDto){

        if (questionTypeDto.getQuestionType().equals(QuestionType.MultiChoice.toString())){
            //adding multiChoice question
            MultiChoiceQuestion multiChoiceQuestion = new MultiChoiceQuestion();

            model.addAttribute("multiChoiceQuestion" , multiChoiceQuestion);
            model.addAttribute("examId" , examId);
            model.addAttribute("examService" , examService);

            return "create-new-multiChoice-question";

        }else {
            //adding detailed question
            DetailedQuestion detailedQuestion = new DetailedQuestion();

            model.addAttribute("detailedQuestion" , detailedQuestion);
            model.addAttribute("examId" , examId);
            model.addAttribute("examService" , examService);

            return "create-new-detailed-question";
        }

    }

    @RequestMapping(value = "/addNewDetailedQuestion/{examId}" , method = RequestMethod.POST)
    public String addNewDetQuestion(Model model ,
                                    @ModelAttribute("detailedQuestion") DetailedQuestion detailedQuestion ,
                                    @PathVariable(value = "examId") Long examId){

        DetailedQuestion newDetailedQuestion = new DetailedQuestion();

        newDetailedQuestion.setText(detailedQuestion.getText());
        newDetailedQuestion.setTitle(detailedQuestion.getTitle());
        newDetailedQuestion.setCreatedDate(detailedQuestion.getCreatedDate());
        newDetailedQuestion.setDefaultScore(detailedQuestion.getDefaultScore());
        newDetailedQuestion.setCreatedBy(examService.findExamById(examId).getCourse().getTeacher());
        newDetailedQuestion.setType(QuestionType.Detailed);

        qService.save(newDetailedQuestion);

        return "redirect:/question/addQuestion/{examId}";

    }

    @RequestMapping(value = "/addNewMultiChoiceQuestion/{examId}" , method = RequestMethod.POST)
    public String addNewMultiChoiceQuestion(Model model ,
                                            @PathVariable(value = "examId") Long examId ,
                                            @ModelAttribute ("multiChoiceQuestion") MultiChoiceQuestion mlcQuestion){

        MultiChoiceQuestion multiChoiceQuestion = new MultiChoiceQuestion();

        multiChoiceQuestion.setText(mlcQuestion.getText());
        multiChoiceQuestion.setTitle(mlcQuestion.getTitle());
        multiChoiceQuestion.setCreatedDate(mlcQuestion.getCreatedDate());
        multiChoiceQuestion.setDefaultScore(mlcQuestion.getDefaultScore());
        multiChoiceQuestion.setCreatedBy(examService.findExamById(examId).getCourse().getTeacher());
        multiChoiceQuestion.setType(QuestionType.MultiChoice);
        List<Answer> answers = new ArrayList<>();
        for (Answer answer : mlcQuestion.getAnswers()){
            if (answer.getText() != "")
                answers.add(answer);
        }
        multiChoiceQuestion.setAnswers(answers);

        qService.save(multiChoiceQuestion);

        model.addAttribute("choicesList" , multiChoiceQuestion.getAnswers());
        model.addAttribute("multiChoiceQuestion" , multiChoiceQuestion);
        model.addAttribute("examId" , examId);

        return "select-correct-answer";
    }

    @RequestMapping(value = "/selectCorrectAnswer/{examId}/{multiChoiceQuestionId}")
    public String selectCorrectAnswer(@PathVariable(value = "multiChoiceQuestionId") Long mlcQuestionId ,
                                      @PathVariable(value = "examId") Long examId ,
                                      @RequestParam(value = "correctAnswerId") Long correctAnswerId){

        MultiChoiceQuestion mlcQuestion = (MultiChoiceQuestion) qService.findQuestionById(mlcQuestionId);

        mlcQuestion.setCorrectAnswer(answerService.findAnswerById(correctAnswerId));
        qService.save(mlcQuestion);

        return "redirect:/question/addQuestion/{examId}";
    }

}
