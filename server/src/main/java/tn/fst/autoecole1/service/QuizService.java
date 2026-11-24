
package tn.fst.autoecole1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.fst.autoecole1.Entity.Quiz;
import tn.fst.autoecole1.repository.QuizRepository;

import java.util.List;

@Service
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public List<Quiz> getAllQuiz() {
        return quizRepository.findAll();
    }

    public Quiz getQuizById(Integer id) {
        return quizRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Quiz introuvable"));
    }

    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public Quiz updateQuiz(Integer id, Quiz quiz) {
        getQuizById(id);
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Integer id) {
        quizRepository.delete(getQuizById(id));
    }
}

