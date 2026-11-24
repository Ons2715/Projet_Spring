package tn.fst.autoecole1.controller;
import tn.fst.autoecole1.Entity.Examen;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "*")
public class ExamenController extends BaseCrudController<Examen> {
    public ExamenController() { super(Examen.class); }
}