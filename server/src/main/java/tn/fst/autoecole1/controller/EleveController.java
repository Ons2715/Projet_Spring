
package tn.fst.autoecole1.controller;

import tn.fst.autoecole1.Entity.Eleve;
import tn.fst.autoecole1.Entity.Formation;
import tn.fst.autoecole1.repository.EleveRepository;
import tn.fst.autoecole1.repository.FormationRepository;
import tn.fst.autoecole1.repository.MoniteurRepository;
import tn.fst.autoecole1.service.AuthSessionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class EleveController extends BaseCrudController<Eleve> {

    private final EleveRepository eleveRepository;
    private final FormationRepository formationRepository;
    private final MoniteurRepository moniteurRepository;
    private final AuthSessionService authSessionService;

    public EleveController(
            EleveRepository eleveRepository,
            FormationRepository formationRepository,
            MoniteurRepository moniteurRepository,
            AuthSessionService authSessionService) {

        super(Eleve.class);
        this.eleveRepository = eleveRepository;
        this.formationRepository = formationRepository;
        this.moniteurRepository = moniteurRepository;
        this.authSessionService = authSessionService;
    }

    @GetMapping("/monitor/me")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getMyCandidates(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Session invalide. Veuillez vous reconnecter."));
        }

        Integer moniteurId = authSessionService.getUserId(authorization.substring(7).trim());
        if (moniteurId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Session invalide. Veuillez vous reconnecter."));
        }
        if (!moniteurRepository.existsById(moniteurId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Cette page est reservee aux moniteurs."));
        }

        List<Map<String, Object>> candidates = new ArrayList<>();
        for (EleveRepository.MonitorStudentView student
                : eleveRepository.findStudentsByMonitorId(moniteurId)) {
            Map<String, Object> candidate = new HashMap<>();
            candidate.put("id", student.getId());
            candidate.put("nom", student.getNom());
            candidate.put("email", student.getEmail());
            candidate.put("telephone", student.getTelephone());
            candidate.put("photo", student.getPhoto());
            candidate.put("formation_nom", student.getFormationNom());
            candidate.put("heures_effectuees", student.getHeuresEffectuees());
            candidate.put("heures_totales", student.getHeuresTotales());
            candidate.put("date_inscription", student.getDateInscription());
            candidates.add(candidate);
        }
        return ResponseEntity.ok(candidates);
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
