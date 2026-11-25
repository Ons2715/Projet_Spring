package tn.fst.autoecole1.controller;

import tn.fst.autoecole1.Entity.Eleve;
import tn.fst.autoecole1.Entity.Formation;
import tn.fst.autoecole1.Entity.Moniteur;
import tn.fst.autoecole1.Entity.Utilisateur;
import tn.fst.autoecole1.repository.EleveRepository;
import tn.fst.autoecole1.repository.MoniteurRepository;
import tn.fst.autoecole1.service.AuthSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitors")
@CrossOrigin(origins = "*")
public class MoniteurController extends BaseCrudController<Moniteur> {
    private final AuthSessionService authSessionService;
    private final EleveRepository eleveRepository;
    private final MoniteurRepository moniteurRepository;

    public MoniteurController(
            AuthSessionService authSessionService,
            EleveRepository eleveRepository,
            MoniteurRepository moniteurRepository) {
        super(Moniteur.class);
        this.authSessionService = authSessionService;
        this.eleveRepository = eleveRepository;
        this.moniteurRepository = moniteurRepository;
    }

    @GetMapping("/me/assigned")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAssignedMonitor(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Token manquant ou invalide"));
        }

        Integer userId = authSessionService.getUserId(authorization.substring(7).trim());
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Session expiree ou invalide"));
        }

        Eleve eleve = eleveRepository.findByIdWithFormation(userId).orElse(null);
        Formation formation = eleve == null ? null : eleve.getFormation();
        if (formation == null) {
            return ResponseEntity.ok(noMonitorResponse(""));
        }

        List<Moniteur> monitors = moniteurRepository
                .findWithUserByFormationId(formation.getId());
        Moniteur formationMonitor = monitors.isEmpty() ? null : monitors.get(0);
        if (formationMonitor == null) {
            return ResponseEntity.ok(noMonitorResponse(formation.getNom()));
        }
        return monitorResponse(formationMonitor, formation);
    }

    private Map<String, Object> noMonitorResponse(String formationName) {
        Map<String, Object> result = new HashMap<>();
        result.put("assigned", false);
        result.put("nom", "");
        result.put("firstName", "");
        result.put("lastName", "");
        result.put("telephone", "");
        result.put("phone", "");
        result.put("email", "");
        result.put("photo", "");
        result.put("formation_nom", formationName);
        result.put("message", formationName.isBlank()
                ? "Aucune formation choisie par cet eleve"
                : "Aucun moniteur disponible pour cette formation");
        return result;
    }

    private ResponseEntity<Map<String, Object>> monitorResponse(
            Moniteur moniteur, Formation formation) {
        Utilisateur utilisateur = moniteur.getUtilisateur();
        if (utilisateur == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Profil utilisateur du moniteur introuvable"));
        }

        Map<String, Object> result = new HashMap<>();
        String nom = utilisateur.getNom() == null ? "" : utilisateur.getNom().trim();
        result.put("assigned", true);
        result.put("id", moniteur.getId());
        result.put("nom", nom);
        result.put("firstName", "");
        result.put("lastName", nom);
        result.put("telephone", utilisateur.getTelephone());
        result.put("phone", utilisateur.getTelephone());
        result.put("email", utilisateur.getEmail());
        result.put("photo", utilisateur.getPhotoProfil());
        result.put("formation_nom", formation.getNom());
        result.put("formation_id", formation.getId());
        return ResponseEntity.ok(result);
    }
}
