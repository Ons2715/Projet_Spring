package tn.fst.autoecole1.controller;
import tn.fst.autoecole1.Entity.TranchePaiement;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/payment-installments")
@CrossOrigin(origins = "*")
public class TranchePaiementController extends BaseCrudController<TranchePaiement> {
    public TranchePaiementController() { super(TranchePaiement.class); }
}