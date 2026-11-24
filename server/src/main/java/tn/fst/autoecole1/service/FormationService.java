
package tn.fst.autoecole1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.fst.autoecole1.Entity.Formation;
import tn.fst.autoecole1.repository.FormationRepository;

import java.util.List;

@Service
@Transactional
public class FormationService {

    private final FormationRepository formationRepository;

    public FormationService(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }

    public Formation getFormationById(Integer id) {
        return formationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Formation introuvable"));
    }

    public Formation saveFormation(Formation formation) {
        return formationRepository.save(formation);
    }

    public Formation updateFormation(Integer id, Formation formation) {
        getFormationById(id);
        return formationRepository.save(formation);
    }

    public void deleteFormation(Integer id) {
        formationRepository.delete(getFormationById(id));
    }
}

