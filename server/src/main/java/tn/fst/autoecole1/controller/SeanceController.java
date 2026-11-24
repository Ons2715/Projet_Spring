package tn.fst.autoecole1.controller;
import tn.fst.autoecole1.Entity.Seance;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*")
public class SeanceController extends BaseCrudController<Seance> {
    public SeanceController() { super(Seance.class); }
}