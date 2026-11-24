package tn.fst.autoecole1.controller;
import tn.fst.autoecole1.Entity.InscriptionExamen;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/exam-registrations")
@CrossOrigin(origins = "*")
public class InscriptionExamenController extends BaseCrudController<InscriptionExamen> {
    public InscriptionExamenController() { super(InscriptionExamen.class); }
}