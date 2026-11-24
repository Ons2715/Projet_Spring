package tn.fst.autoecole1.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import tn.fst.autoecole1.Entity.Formation;
import tn.fst.autoecole1.Entity.Enum.Transmission;
import tn.fst.autoecole1.Entity.Enum.TypePratique;
import tn.fst.autoecole1.repository.FormationRepository;

@Component
public class FormationDataInitializer implements CommandLineRunner {

    private final FormationRepository formationRepository;

    public FormationDataInitializer(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    @Override
    public void run(String... args) {
        List<FormationSeed> formations = List.of(
                new FormationSeed("Formation initiale manuelle", TypePratique.FORMATION_INITIALE, Transmission.MANUELLE, 20, List.of("Formation initiale", "Permis B boîte manuelle")),
                new FormationSeed("Formation initiale automatique", TypePratique.FORMATION_INITIALE, Transmission.AUTOMATIQUE, 20, List.of("Permis B boîte automatique")),
                new FormationSeed("Recyclage manuelle", TypePratique.RECYCLAGE, Transmission.MANUELLE, 7, List.of("Recyclage")),
                new FormationSeed("Recyclage automatique", TypePratique.RECYCLAGE, Transmission.AUTOMATIQUE, 7, List.of("Code"))
        );

        for (FormationSeed seed : formations) {
            Formation formation = findExistingRecord(seed);

            if (formation == null) {
                formation = new Formation();
                formation.setPrix(BigDecimal.ZERO);
            }

            formation.setNom(seed.nom());
            formation.setTypePratique(seed.typePratique());
            formation.setTransmission(seed.transmission());
            formation.setHeuresTotales(seed.heuresTotales());
            formationRepository.save(formation);
        }
    }

    private Formation findExistingRecord(FormationSeed seed) {
        Formation existing = formationRepository.findByNom(seed.nom()).orElse(null);
        if (existing != null) return existing;

        for (String oldName : seed.legacyNames()) {
            existing = formationRepository.findByNom(oldName).orElse(null);
            if (existing != null) return existing;
        }

        return formationRepository
                .findByTypePratiqueAndTransmission(seed.typePratique(), seed.transmission())
                .orElse(null);
    }

    private record FormationSeed(
            String nom,
            TypePratique typePratique,
            Transmission transmission,
            int heuresTotales,
            List<String> legacyNames
    ) { }
}
