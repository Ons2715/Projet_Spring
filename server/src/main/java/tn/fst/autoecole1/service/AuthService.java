package tn.fst.autoecole1.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.fst.autoecole1.Entity.Utilisateur;
import tn.fst.autoecole1.Entity.Eleve;
import tn.fst.autoecole1.Entity.Enum.Role;

import tn.fst.autoecole1.dto.auth.RegisterRequest;
import tn.fst.autoecole1.dto.auth.LoginRequest;

import tn.fst.autoecole1.exception.BusinessException;
import tn.fst.autoecole1.repository.UtilisateurRepository;
import tn.fst.autoecole1.repository.EleveRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final EleveRepository eleveRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthSessionService authSessionService;

    public AuthService(
            UtilisateurRepository utilisateurRepository,
            EleveRepository eleveRepository,
            PasswordEncoder passwordEncoder,
            AuthSessionService authSessionService) {

        this.utilisateurRepository = utilisateurRepository;
        this.eleveRepository = eleveRepository;
        this.passwordEncoder = passwordEncoder;
        this.authSessionService = authSessionService;
    }

    @Transactional
    public Map<String, Object> register(RegisterRequest request) {

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

        // Une inscription publique crée toujours un élève.
        utilisateur.setRole(Role.eleve);

        utilisateur = utilisateurRepository.save(utilisateur);

        Eleve eleve = new Eleve();
        eleve.setUtilisateur(utilisateur);
        eleve.setDateInscription(LocalDate.now());

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
