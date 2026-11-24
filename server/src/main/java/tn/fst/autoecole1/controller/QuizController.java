package tn.fst.autoecole1.controller;
import tn.fst.autoecole1.Entity.Quiz;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "*")
public class QuizController extends BaseCrudController<Quiz> {
    public QuizController() { super(Quiz.class); }
}