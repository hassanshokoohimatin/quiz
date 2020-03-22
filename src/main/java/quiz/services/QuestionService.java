package quiz.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.model.Question;
import quiz.repositories.QuestionRepository;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> findAll(){
        return questionRepository.findAll();
    }

    public Question findQuestionById(Long id){
        return questionRepository.findById(id).get();
    }

    public void removeById(Long id){
        questionRepository.deleteById(id);
    }

    public Question save(Question question){
         return questionRepository.save(question);
    }

    public List<Question> findQuestionsByTextContaining(String text){
        return questionRepository.findQuestionsByTextContaining(text);
    }

    public List<Question> findByTitle(String title){
        return questionRepository.findQuestionsByTitle(title);
    }

    public List<Question> findQuestionsByTeacherId(Long teacherId){
        return questionRepository.findQuestionsByCreatedBy_Id(teacherId);
    }
}
