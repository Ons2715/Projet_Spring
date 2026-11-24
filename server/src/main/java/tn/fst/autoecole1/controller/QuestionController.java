package tn.fst.autoecole1.controller;
import tn.fst.autoecole1.Entity.Question;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController extends BaseCrudController<Question> {
    public QuestionController() { super(Question.class); }
}