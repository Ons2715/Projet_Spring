package tn.fst.autoecole1.controller;

import tn.fst.autoecole1.Entity.Eleve;
import tn.fst.autoecole1.Entity.Moniteur;
import tn.fst.autoecole1.Entity.Utilisateur;
import tn.fst.autoecole1.repository.EleveRepository;
import tn.fst.autoecole1.repository.MoniteurRepository;
import tn.fst.autoecole1.repository.UtilisateurRepository;
import tn.fst.autoecole1.service.AuthSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UtilisateurController extends BaseCrudController<Utilisateur> {

    private final UtilisateurRepository utilisateurRepository;
    private final EleveRepository eleveRepository;
    private final MoniteurRepository moniteurRepository;
    private final AuthSessionService authSessionService;

    public UtilisateurController(
            UtilisateurRepository utilisateurRepository,
            EleveRepository eleveRepository,
            MoniteurRepository moniteurRepository,
            AuthSessionService authSessionService) {
        super(Utilisateur.class);
        this.utilisateurRepository = utilisateurRepository;
        this.eleveRepository = eleveRepository;
        this.moniteurRepository = moniteurRepository;
        this.authSessionService = authSessionService;
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getMyProfile(
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {
        Integer userId = getUserId(authorization);
        if (userId == null) {
            return unauthorized();
        }

        Utilisateur utilisateur = utilisateurRepository.findById(userId).orElse(null);
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toProfileResponse(utilisateur));
    }

    @PatchMapping("/me")
    @Transactional
    public ResponseEntity<?> updateMyProfile(
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @RequestBody ProfileUpdateRequest request) {
        Integer userId = getUserId(authorization);
        if (userId == null) {
            return unauthorized();
        }
        if (request == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Données de profil manquantes."));
        }

        Utilisateur utilisateur = utilisateurRepository.findById(userId).orElse(null);
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.firstName != null || request.lastName != null) {
            String[] currentName = utilisateur.getNom().trim().split("\\s+", 2);
            String firstName = request.firstName == null
                    ? currentName[0]
                    : request.firstName.trim();
            String lastName = request.lastName == null
                    ? currentName.length > 1 ? currentName[1] : ""
                    : request.lastName.trim();
            String fullName = (firstName + " " + lastName).trim();
            if (firstName.isBlank() || fullName.length() > 100) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Nom invalide."));
            }
            utilisateur.setNom(fullName);
        }

        if (request.email != null) {
            String email = request.email.trim();
            if (email.isBlank() || email.length() > 100) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Adresse email invalide."));
            }
            Optional<Utilisateur> existing = utilisateurRepository.findByEmail(email);
            if (existing.isPresent() && !existing.get().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Cette adresse email existe déjà."));
            }
            utilisateur.setEmail(email);
        }

        if (request.telephone != null) {
            if (request.telephone.isBlank() || request.telephone.trim().length() > 15) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Téléphone invalide."));
            }
            utilisateur.setTelephone(request.telephone.trim());
        }
        if (request.adresse != null) {
            utilisateur.setAdresse(request.adresse.trim());
        }

        return ResponseEntity.ok(
                toProfileResponse(utilisateurRepository.saveAndFlush(utilisateur))
        );
    }

    @PatchMapping("/me/photo")
    @Transactional
    public ResponseEntity<?> updateMyPhoto(
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @RequestBody PhotoUpdateRequest request) {
        Integer userId = getUserId(authorization);
        if (userId == null) {
            return unauthorized();
        }
        if (request == null || request.photo == null || request.photo.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Photo manquante."));
        }
        if (request.photo.length() > 2_000_000) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La photo est trop volumineuse."));
        }

        Utilisateur utilisateur = utilisateurRepository.findById(userId).orElse(null);
        if (utilisateur == null) {
            return ResponseEntity.notFound().build();
        }
        utilisateur.setPhotoProfil(request.photo);
        return ResponseEntity.ok(
                toProfileResponse(utilisateurRepository.saveAndFlush(utilisateur))
        );
    }

    private Integer getUserId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7).trim();
        return token.isEmpty() ? null : authSessionService.getUserId(token);
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Session invalide. Veuillez vous reconnecter."));
    }

    private Map<String, Object> toProfileResponse(Utilisateur utilisateur) {
        Map<String, Object> response = new HashMap<>();
        String[] nameParts = utilisateur.getNom().trim().split("\\s+", 2);

        response.put("id", utilisateur.getId());
        response.put("nom", utilisateur.getNom());
        response.put("name", utilisateur.getNom());
        response.put("firstName", nameParts[0]);
        response.put("lastName", nameParts.length > 1 ? nameParts[1] : "");
        response.put("email", utilisateur.getEmail());
        response.put("telephone", utilisateur.getTelephone());
        response.put("phone", utilisateur.getTelephone());
        response.put("adresse", utilisateur.getAdresse());
        response.put("address", utilisateur.getAdresse());
        response.put("photo", utilisateur.getPhotoProfil());
        response.put("role", utilisateur.getRole().name());

        Eleve eleve = eleveRepository.findById(utilisateur.getId()).orElse(null);
        if (eleve != null) {
            response.put("heures_effectuees", eleve.getHeuresEffectuees());
            if (eleve.getFormation() != null) {
                response.put("formation_nom", eleve.getFormation().getNom());
                response.put("heures_totales", eleve.getFormation().getHeuresTotales());
            }
        }

        Moniteur moniteur = moniteurRepository.findById(utilisateur.getId()).orElse(null);
        if (moniteur != null && moniteur.getFormation() != null) {
            response.put("formation_nom", moniteur.getFormation().getNom());
            response.put("id_formation", moniteur.getFormation().getId());
            if (moniteur.getVoiture() != null) {
                response.put("voiture", moniteur.getVoiture().name());
            }
        }
        return response;
    }

    public static class ProfileUpdateRequest {
        public String firstName;
        public String lastName;
        public String email;
        public String telephone;
        public String adresse;
    }

    public static class PhotoUpdateRequest {
        public String photo;
    }
}