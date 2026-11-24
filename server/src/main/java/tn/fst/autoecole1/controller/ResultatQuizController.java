package tn.fst.autoecole1.controller;
import tn.fst.autoecole1.Entity.ResultatQuiz;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/quiz-results")
@CrossOrigin(origins = "*")
public class ResultatQuizController extends BaseCrudController<ResultatQuiz> {
    public ResultatQuizController() { super(ResultatQuiz.class); }
}