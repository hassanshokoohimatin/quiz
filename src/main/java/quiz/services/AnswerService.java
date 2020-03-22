package quiz.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.model.Answer;
import quiz.repositories.AnswerRepository;

import java.util.List;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    public List<Answer> findAll(){
        return answerRepository.findAll();
    }

    public Answer findAnswerById(Long id){
        return answerRepository.findById(id).get();
    }
}
