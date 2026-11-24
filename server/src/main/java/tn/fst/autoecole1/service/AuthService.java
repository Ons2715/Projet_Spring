package tn.fst.autoecole1.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.fst.autoecole1.Entity.Utilisateur;
import tn.fst.autoecole1.Entity.Eleve;
import tn.fst.autoecole1.Entity.Formation;
import tn.fst.autoecole1.Entity.Enum.Role;

import tn.fst.autoecole1.dto.auth.RegisterRequest;
import tn.fst.autoecole1.dto.auth.LoginRequest;

import tn.fst.autoecole1.exception.BusinessException;
import tn.fst.autoecole1.repository.UtilisateurRepository;
import tn.fst.autoecole1.repository.EleveRepository;
import tn.fst.autoecole1.repository.FormationRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final EleveRepository eleveRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthSessionService authSessionService;
    private final FormationRepository formationRepository;

    public AuthService(
            UtilisateurRepository utilisateurRepository,
            EleveRepository eleveRepository,
            PasswordEncoder passwordEncoder,
            AuthSessionService authSessionService,
            FormationRepository formationRepository) {

        this.utilisateurRepository = utilisateurRepository;
        this.eleveRepository = eleveRepository;
        this.passwordEncoder = passwordEncoder;
        this.authSessionService = authSessionService;
        this.formationRepository = formationRepository;
    }

    @Transactional
    public Map<String, Object> register(RegisterRequest request) {

        if (request.formationId == null || request.photo == null || request.photo.isBlank()) {
            throw new BusinessException(
                    "Choisissez une formation et ajoutez une photo avant de finaliser l'inscription.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (utilisateurRepository.existsByEmail(request.email)) {
            throw new BusinessException(
                    "Cette adresse email existe déjà.",
                    HttpStatus.CONFLICT
            );
        }

        Utilisateur utilisateur = new Utilisateur();

        utilisateur.setNom(request.firstName + " " + request.lastName);
        utilisateur.setEmail(request.email);
        utilisateur.setTelephone(request.telephone);
        utilisateur.setAdresse(
                request.adresse != null ? request.adresse : ""
        );
        utilisateur.setMotDePasse(
                passwordEncoder.encode(request.password)
        );
        if (request.photo != null && request.photo.length() > 2_000_000) {
            throw new BusinessException("La photo est trop volumineuse.", HttpStatus.BAD_REQUEST);
        }
        utilisateur.setPhotoProfil(request.photo);

        // Une inscription publique crée toujours un élève.
        utilisateur.setRole(Role.eleve);

        utilisateur = utilisateurRepository.save(utilisateur);

        Eleve eleve = new Eleve();
        eleve.setUtilisateur(utilisateur);
        eleve.setDateInscription(LocalDate.now());
        if (request.formationId != null) {
            Formation formation = formationRepository.findById(request.formationId)
                    .orElseThrow(() -> new BusinessException("Formation introuvable.", HttpStatus.BAD_REQUEST));
            eleve.setFormation(formation);
        }

        eleveRepository.save(eleve);

        return createResponse(utilisateur);
    }

    public Map<String, Object> login(LoginRequest request) {

        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(request.email)
                .orElseThrow(() -> new BusinessException(
                        "Email ou mot de passe incorrect.",
                        HttpStatus.UNAUTHORIZED
                ));

        if (!passwordEncoder.matches(
                request.password,
                utilisateur.getMotDePasse())) {

            throw new BusinessException(
                    "Email ou mot de passe incorrect.",
                    HttpStatus.UNAUTHORIZED
            );
        }

        return createResponse(utilisateur);
    }

    private Map<String, Object> createResponse(Utilisateur utilisateur) {

        Map<String, Object> user = new HashMap<>();

        user.put("id", utilisateur.getId());
        user.put("name", utilisateur.getNom());

        // Conservé comme dans l'ancienne API pour ne pas casser le frontend.
        user.put("firstName", utilisateur.getNom());

        user.put("email", utilisateur.getEmail());
        user.put("telephone", utilisateur.getTelephone());
        user.put("adresse", utilisateur.getAdresse());
        user.put("role", utilisateur.getRole().name());

        Map<String, Object> response = new HashMap<>();

        response.put("user", user);
        response.put(
                "token",
                authSessionService.createSession(utilisateur.getId())
        );

        return response;
    }
}
