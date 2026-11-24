
package tn.fst.autoecole1.controller;

import tn.fst.autoecole1.Entity.Eleve;
import tn.fst.autoecole1.Entity.Formation;
import tn.fst.autoecole1.repository.EleveRepository;
import tn.fst.autoecole1.repository.FormationRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class EleveController extends BaseCrudController<Eleve> {

    private final EleveRepository eleveRepository;
    private final FormationRepository formationRepository;

    public EleveController(
            EleveRepository eleveRepository,
            FormationRepository formationRepository) {

        super(Eleve.class);
        this.eleveRepository = eleveRepository;
        this.formationRepository = formationRepository;
    }

    // DTO pour choisir une formation
    public static class FormationRequest {
        public Integer formationId;
    }

    // Affecter une formation à un élève
    @PutMapping("/{eleveId}/formation")
    public ResponseEntity<?> choisirFormation(
            @PathVariable Integer eleveId,
            @RequestBody FormationRequest request) {

        if (request.formationId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Veuillez choisir une formation."));
        }

        var eleveOpt = eleveRepository.findById(eleveId);

        if (eleveOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var formationOpt =
                formationRepository.findById(request.formationId);

        if (formationOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Formation introuvable."));
        }

        Eleve eleve = eleveOpt.get();
        Formation formation = formationOpt.get();

        eleve.setFormation(formation);
        eleveRepository.save(eleve);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Formation affectée avec succès.",
                        "eleveId", eleve.getId(),
                        "formationId", formation.getId()
                )
        );
    }
}
