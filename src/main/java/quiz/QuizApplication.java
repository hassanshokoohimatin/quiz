package quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import quiz.model.ForgotPasswordQuestion;
import quiz.services.ForgotPasswordQuestionService;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
public class QuizApplication {

    public static void main(String[] args) {

//
//        String text1 = "what is your favorite niece name?";
//        String text2 = "what is your first school teacher?";
//
//        List<String> texts = new ArrayList<>();
//
//        texts.add(text1);
//        texts.add(text2);
//
//        ForgotPasswordQuestionService forgotPasswordQuestionService = new ForgotPasswordQuestionService();
//
//        for (String t : texts){
//            ForgotPasswordQuestion fpq = new ForgotPasswordQuestion();
//            fpq.setDescription(t);
//            forgotPasswordQuestionService.save(fpq);
//        }


        SpringApplication.run(QuizApplication.class, args);


    }
}
