

package tn.fst.autoecole1.controller;

import tn.fst.autoecole1.Entity.Moniteur;
import tn.fst.autoecole1.Entity.Reservation;
import tn.fst.autoecole1.Entity.Utilisateur;
import tn.fst.autoecole1.Entity.Eleve;

import tn.fst.autoecole1.repository.EleveRepository;
import tn.fst.autoecole1.repository.MoniteurRepository;
import tn.fst.autoecole1.repository.ReservationRepository;
import tn.fst.autoecole1.service.AuthSessionService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/monitors")
@CrossOrigin(origins = "*")
public class MoniteurController extends BaseCrudController<Moniteur> {

    private final AuthSessionService authSessionService;
    private final ReservationRepository reservationRepository;
    private final EleveRepository eleveRepository;
    private final MoniteurRepository moniteurRepository;

    public MoniteurController(
            AuthSessionService authSessionService,
            ReservationRepository reservationRepository,
            EleveRepository eleveRepository,
            MoniteurRepository moniteurRepository) {

        super(Moniteur.class);
        this.authSessionService = authSessionService;
        this.reservationRepository = reservationRepository;
        this.eleveRepository = eleveRepository;
        this.moniteurRepository = moniteurRepository;
    }

    @GetMapping("/me/assigned")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAssignedMonitor(
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Token manquant ou invalide"));
        }

        String token = authorization.substring(7).trim();

        Integer userId = authSessionService.getUserId(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Session expirée ou invalide"));
        }

        // Récupérer les réservations de l'élève connecté
        List<Reservation> reservations =
                reservationRepository.findByEleveIdOrderByDateReservation(userId);

        // Chercher une réservation avec un moniteur
        Reservation assignedReservation = reservations.stream()
                .filter(r -> r.getStatut() != null)
                .filter(r -> !r.getStatut().name()
                        .equalsIgnoreCase("ANNULEE"))
                .filter(r -> r.getMoniteur() != null)
                .findFirst()
                .orElse(null);

        if (assignedReservation != null) {
            Moniteur moniteur = assignedReservation.getMoniteur();

            if (moniteur.getUtilisateur() != null) {
                return monitorResponse(moniteur, true);
            }
        }

        // Si aucune réservation n'a de moniteur,
        // chercher le moniteur de la formation de l'élève
        Eleve eleve = eleveRepository.findById(userId).orElse(null);

        if (eleve != null && eleve.getFormation() != null) {
            List<Moniteur> monitors =
                    moniteurRepository.findByFormationId(
                            eleve.getFormation().getId());

            Moniteur formationMonitor = monitors.stream()
                    .filter(m -> m.getUtilisateur() != null)
                    .findFirst()
                    .orElse(null);

            if (formationMonitor != null) {
                return monitorResponse(formationMonitor, false);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("assigned", false);
        result.put("nom", "");
        result.put("firstName", "");
        result.put("lastName", "");
        result.put("telephone", "");
        result.put("phone", "");
        result.put("email", "");
        result.put("photo", "");
        result.put("message", "Aucun moniteur affecté à cet élève");

        return ResponseEntity.ok(result);
    }

    private ResponseEntity<Map<String, Object>> monitorResponse(
            Moniteur moniteur,
            boolean assigned) {

        Utilisateur utilisateur = moniteur.getUtilisateur();

        if (utilisateur == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", "Profil utilisateur du moniteur introuvable"
                    ));
        }

        Map<String, Object> result = new HashMap<>();

        String nom = utilisateur.getNom() == null
                ? ""
                : utilisateur.getNom().trim();

        result.put("assigned", assigned);
        result.put("id", moniteur.getId());
        result.put("nom", nom);
        result.put("firstName", "");
        result.put("lastName", nom);
        result.put("telephone", utilisateur.getTelephone());
        result.put("phone", utilisateur.getTelephone());
        result.put("email", utilisateur.getEmail());
        result.put("photo", utilisateur.getPhotoProfil());

        return ResponseEntity.ok(result);
    }
}
