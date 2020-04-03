package quiz.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.model.ForgotPasswordQuestion;
import quiz.repositories.ForgotPasswordQuestionRepository;

import java.util.List;

@Service
public class ForgotPasswordQuestionService {

    @Autowired
    private ForgotPasswordQuestionRepository forgotPasswordQuestionRepository;

    public List<ForgotPasswordQuestion> findAll(){
        return forgotPasswordQuestionRepository.findAll();
    }

    public void save(ForgotPasswordQuestion forgotPasswordQuestion){
        forgotPasswordQuestionRepository.save(forgotPasswordQuestion);
    }
}
