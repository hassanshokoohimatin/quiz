package quiz.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.model.Exam;
import quiz.repositories.ExamRepository;

import java.util.List;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    public Exam findExamById(Long id){
        return examRepository.findById(id).get();
    }

    public void saveExam(Exam exam){
        examRepository.save(exam);
    }

    public void removeExamById(Long id){
        examRepository.deleteById(id);
    }

    public void removeExam(Exam exam){
        examRepository.delete(exam);
    }

    public List<Exam> findAllExams(){
        return examRepository.findAll();
    }

    public List<Exam> findExamsByCourseId(Long id){
        return examRepository.findExamsByCourse_Id(id);
    }

}
