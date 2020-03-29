package quiz.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.model.ExamPaper;
import quiz.repositories.ExamPaperRepository;

import java.util.List;

@Service
public class ExamPaperService {

    @Autowired
    private ExamPaperRepository examPaperRepository;

    public List<ExamPaper> findAll(){
        return examPaperRepository.findAll();
    }

    public ExamPaper findById(Long id){
        return examPaperRepository.findById(id).get();
    }

    public ExamPaper findExamPaperOfAnStudentInOneExam(Long examId , Long studentId){
        return examPaperRepository.findExamPaperByExamIdAndStudentId(examId , studentId);
    }

    public List<ExamPaper> findExamPapersOfAnStudent(Long studentId){
        return examPaperRepository.findExamPapersByStudentId(studentId);
    }

    public void save(ExamPaper examPaper){
        examPaperRepository.save(examPaper);
    }
}
