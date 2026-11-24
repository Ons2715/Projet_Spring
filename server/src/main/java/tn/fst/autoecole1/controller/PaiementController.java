package tn.fst.autoecole1.controller;
import tn.fst.autoecole1.Entity.Paiement;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaiementController extends BaseCrudController<Paiement> {
    public PaiementController() { super(Paiement.class); }
}