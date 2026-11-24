package tn.fst.autoecole1.controller;

import tn.fst.autoecole1.Entity.Formation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/formations")
@CrossOrigin(origins = "*")
public class FormationController extends BaseCrudController<Formation> {
    public FormationController() { super(Formation.class); }
}