package tn.fst.autoecole1.controller;
import tn.fst.autoecole1.Entity.OptionReponse;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/options")
@CrossOrigin(origins = "*")
public class OptionReponseController extends BaseCrudController<OptionReponse> {
    public OptionReponseController() { super(OptionReponse.class); }
}