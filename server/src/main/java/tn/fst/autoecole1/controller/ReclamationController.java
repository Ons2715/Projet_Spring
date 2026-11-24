package tn.fst.autoecole1.controller;

import tn.fst.autoecole1.Entity.Enum.StatutReclamation;
import tn.fst.autoecole1.Entity.Reclamation;
import tn.fst.autoecole1.Entity.Reservation;
import tn.fst.autoecole1.Entity.Utilisateur;
import tn.fst.autoecole1.repository.ReclamationRepository;
import tn.fst.autoecole1.repository.ReservationRepository;
import tn.fst.autoecole1.repository.UtilisateurRepository;
import tn.fst.autoecole1.service.AuthSessionService;
import tn.fst.autoecole1.service.StorageService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/reclamations")
@CrossOrigin(origins = "*")
public class ReclamationController {

    private static final Pattern RESERVATION_REFERENCE =
            Pattern.compile("^booking#(\\d+)$");

    private final ReclamationRepository reclamationRepository;
    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AuthSessionService authSessionService;
    private final StorageService storageService;

    public ReclamationController(
            ReclamationRepository reclamationRepository,
            ReservationRepository reservationRepository,
            UtilisateurRepository utilisateurRepository,
            AuthSessionService authSessionService,
            StorageService storageService) {
        this.reclamationRepository = reclamationRepository;
        this.reservationRepository = reservationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.authSessionService = authSessionService;
        this.storageService = storageService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAll(
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {
        Utilisateur currentUser = getCurrentUser(authorization);
        if (currentUser == null) {
            return unauthorized();
        }
        if (currentUser.getRole() != tn.fst.autoecole1.Entity.Enum.Role.admin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé à l'administrateur."));
        }

        List<Map<String, Object>> responses = reclamationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @RequestBody CreateReclamationRequest request) {
        Utilisateur utilisateur = getCurrentUser(authorization);
        if (utilisateur == null) {
            return unauthorized();
        }
        if (request == null || request.raison == null || request.raison.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Veuillez saisir la raison de la réclamation."));
        }

        Reclamation reclamation = new Reclamation();
        reclamation.setUtilisateur(utilisateur);
        reclamation.setRaison(request.raison.trim());
        reclamation.setStatut(StatutReclamation.nouvelle);

        if (request.reservationRef != null) {
            Matcher matcher = RESERVATION_REFERENCE.matcher(request.reservationRef.trim());
            if (matcher.matches()) {
                Integer reservationId;
                try {
                    reservationId = Integer.valueOf(matcher.group(1));
                } catch (NumberFormatException exception) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Réservation invalide."));
                }

                Reservation reservation =
                        reservationRepository.findById(reservationId).orElse(null);
                if (reservation == null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Réservation introuvable."));
                }
                if (!reservation.getEleve().getId().equals(utilisateur.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Cette réservation ne vous appartient pas."));
                }
                reclamation.setReservation(reservation);
            }
        }

        if (request.pieceData != null && !request.pieceData.isBlank()) {
            String[] dataUrl = request.pieceData.split(",", 2);
            if (dataUrl.length != 2 || !dataUrl[0].startsWith("data:")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Format de pièce jointe invalide."));
            }

            byte[] attachment;
            try {
                attachment = Base64.getDecoder().decode(dataUrl[1]);
            } catch (IllegalArgumentException exception) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Contenu de pièce jointe invalide."));
            }
            if (attachment.length > 2 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La pièce jointe ne doit pas dépasser 2 Mo."));
            }

            reclamation.setPieceNom(request.pieceNom);
            reclamation.setPieceType(request.pieceType);
            reclamation.setPieceChemin(
                    storageService.store(attachment, request.pieceNom)
            );
        }

        Reclamation saved = reclamationRepository.saveAndFlush(reclamation);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PatchMapping("/{id}/traiter")
    @Transactional
    public ResponseEntity<?> treat(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {
        Utilisateur currentUser = getCurrentUser(authorization);
        if (currentUser == null) {
            return unauthorized();
        }
        if (currentUser.getRole() != tn.fst.autoecole1.Entity.Enum.Role.admin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé à l'administrateur."));
        }

        Reclamation reclamation = reclamationRepository.findById(id).orElse(null);
        if (reclamation == null) {
            return ResponseEntity.notFound().build();
        }

        reclamation.setStatut(StatutReclamation.traitee);
        return ResponseEntity.ok(
                Map.of("id", reclamation.getId(), "statut", reclamation.getStatut().name())
        );
    }

    private Utilisateur getCurrentUser(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7).trim();
        if (token.isEmpty()) {
            return null;
        }
        Integer userId = authSessionService.getUserId(token);
        return userId == null
                ? null
                : utilisateurRepository.findById(userId).orElse(null);
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Session invalide. Veuillez vous reconnecter."));
    }

    private Map<String, Object> toResponse(Reclamation reclamation) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", reclamation.getId());
        response.put("raison", reclamation.getRaison());
        response.put("statut", reclamation.getStatut().name());
        response.put("date_creation", reclamation.getDateCreation());
        response.put("piece_nom", reclamation.getPieceNom());
        response.put("utilisateur_nom", reclamation.getUtilisateur() == null
                ? null
                : reclamation.getUtilisateur().getNom());
        response.put("utilisateur_email", reclamation.getUtilisateur() == null
                ? null
                : reclamation.getUtilisateur().getEmail());
        response.put("utilisateur_telephone", reclamation.getUtilisateur() == null
                ? null
                : reclamation.getUtilisateur().getTelephone());

        Reservation reservation = reclamation.getReservation();
        if (reservation != null && reservation.getMoniteur() != null
                && reservation.getMoniteur().getUtilisateur() != null) {
            response.put("moniteur_nom",
                    reservation.getMoniteur().getUtilisateur().getNom());
            response.put("moniteur_telephone",
                    reservation.getMoniteur().getUtilisateur().getTelephone());
        }

        if (reclamation.getPieceChemin() != null) {
            String contentType = reclamation.getPieceType();
            if (contentType == null || contentType.isBlank()
                    || !contentType.matches("[A-Za-z0-9.+-]+/[A-Za-z0-9.+-]+")) {
                contentType = "application/octet-stream";
            }
            response.put("piece_data", "data:" + contentType + ";base64,"
                    + Base64.getEncoder().encodeToString(
                            storageService.load(reclamation.getPieceChemin())
                    ));
        }
        return response;
    }

    public static class CreateReclamationRequest {
        public Integer utilisateurId;
        public String reservationRef;
        public String raison;
        public String pieceNom;
        public String pieceType;
        public String pieceData;
    }
}
