package tn.fst.autoecole1;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import tn.fst.autoecole1.Entity.Enum.Role;
import tn.fst.autoecole1.Entity.Enum.Transmission;
import tn.fst.autoecole1.Entity.Enum.TypePratique;
import tn.fst.autoecole1.Entity.Enum.Voiture;
import tn.fst.autoecole1.Entity.Formation;
import tn.fst.autoecole1.Entity.Moniteur;
import tn.fst.autoecole1.Entity.Utilisateur;
import tn.fst.autoecole1.repository.FormationRepository;
import tn.fst.autoecole1.repository.MoniteurRepository;
import tn.fst.autoecole1.repository.UtilisateurRepository;

@SpringBootApplication
public class Autoecole1Application {

    public static void main(String[] args) {
        SpringApplication.run(Autoecole1Application.class, args);
    }

    @Bean
    CommandLineRunner initData(
            FormationRepository formationRepository,
            UtilisateurRepository utilisateurRepository,
            MoniteurRepository moniteurRepository,
            PasswordEncoder passwordEncoder,
            PlatformTransactionManager transactionManager) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return args -> transactionTemplate.executeWithoutResult(status -> {
            // 1) Formations
            List<FormationSeed> formationSeeds = List.of(
                    new FormationSeed("Formation initiale boite manuelle",
                            Transmission.MANUELLE, TypePratique.FORMATION_INITIALE),
                    new FormationSeed("Formation initiale boite automatique",
                            Transmission.AUTOMATIQUE, TypePratique.FORMATION_INITIALE),
                    new FormationSeed("Recyclage boite manuelle",
                            Transmission.MANUELLE, TypePratique.RECYCLAGE),
                    new FormationSeed("Recyclage boite automatique",
                            Transmission.AUTOMATIQUE, TypePratique.RECYCLAGE)
            );

            long formationCount = formationRepository.count();
            if (formationCount == 0) {
                for (FormationSeed seed : formationSeeds) {
                    Formation formation = new Formation();
                    formation.setNom(seed.nom());
                    formation.setPrix(BigDecimal.ZERO); // Replace with the real prices.
                    formation.setHeuresTotales(20); // Adjust recycling hours if needed.
                    formation.setTransmission(seed.transmission());
                    formation.setTypePratique(seed.typePratique());
                    formationRepository.save(formation);
                }
            } else if (formationCount < 4) {
                for (FormationSeed seed : formationSeeds) {
                    if (formationRepository.count() >= 4) {
                        break;
                    }
                    if (formationRepository.existsByNom(seed.nom())) {
                        continue;
                    }

                    Formation formation = new Formation();
                    formation.setNom(seed.nom());
                    formation.setPrix(BigDecimal.ZERO);
                    formation.setHeuresTotales(20);
                    formation.setTransmission(seed.transmission());
                    formation.setTypePratique(seed.typePratique());
                    formationRepository.save(formation);
                }
            }

            // 2) One monitor per formation, ordered by formation ID.
            List<Formation> formations = formationRepository.findAll(Sort.by("id"));
            if (formations.size() < 4) {
                throw new IllegalStateException("Il faut 4 formations en base.");
            }

            List<MoniteurSeed> monitorSeeds = List.of(
                    new MoniteurSeed("Ahmed Ben Ali", "ahmedbenali@autoecole.tn",
                            "20111111", Voiture.RENAULT_CLIO_4, "ahmed2005.."),
                    new MoniteurSeed("Sami Trabelsi", "samitrabelsi@autoecole.tn",
                            "20222222", Voiture.PEUGEOT_208, "sami2005.."),
                    new MoniteurSeed("Karim Jlassi", "karimjlassi@autoecole.tn",
                            "20333333", Voiture.VOLKSWAGEN_POLO, "karim2005.."),
                    new MoniteurSeed("Nadia Mansouri", "nadiamansouri@autoecole.tn",
                            "20444444", Voiture.KIA_PICANTO, "nadia2005..")
            );

            for (int i = 0; i < monitorSeeds.size(); i++) {
                MoniteurSeed seed = monitorSeeds.get(i);
                Utilisateur utilisateur;
                if (utilisateurRepository.existsByEmail(seed.email())) {
                    utilisateur = utilisateurRepository.findByEmail(seed.email()).orElse(null);
                    if (utilisateur == null
                            || utilisateur.getRole() != Role.moniteur
                            || moniteurRepository.existsById(utilisateur.getId())) {
                        continue;
                    }
                } else {
                    utilisateur = new Utilisateur();
                    utilisateur.setNom(seed.nom());
                    utilisateur.setEmail(seed.email());
                    utilisateur.setTelephone(seed.telephone());
                    utilisateur.setAdresse("Tunis");
                    utilisateur.setMotDePasse(passwordEncoder.encode(seed.motDePasse()));
                    utilisateur.setRole(Role.moniteur);
                    utilisateur.setActif(true);
                    utilisateur = utilisateurRepository.save(utilisateur);
                }

                Moniteur moniteur = new Moniteur();
                moniteur.setUtilisateur(utilisateur);
                moniteur.setFormation(formations.get(i));
                moniteur.setVoiture(seed.voiture());
                moniteurRepository.save(moniteur);
            }
        });
    }

    private record FormationSeed(
            String nom,
            Transmission transmission,
            TypePratique typePratique) { }

    private record MoniteurSeed(
            String nom,
            String email,
            String telephone,
            Voiture voiture,
            String motDePasse) { }
}
