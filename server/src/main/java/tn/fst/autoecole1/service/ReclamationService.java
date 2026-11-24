
package tn.fst.autoecole1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.fst.autoecole1.Entity.Reclamation;
import tn.fst.autoecole1.repository.ReclamationRepository;

import java.util.List;

@Service
@Transactional
public class ReclamationService {

    private final ReclamationRepository reclamationRepository;

    public ReclamationService(
            ReclamationRepository reclamationRepository) {
        this.reclamationRepository = reclamationRepository;
    }

    public List<Reclamation> getAllReclamations() {
        return reclamationRepository.findAll();
    }

    public Reclamation getReclamationById(Integer id) {
        return reclamationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Réclamation introuvable"));
    }

    public Reclamation saveReclamation(Reclamation reclamation) {
        return reclamationRepository.save(reclamation);
    }

    public Reclamation updateReclamation(
            Integer id, Reclamation reclamation) {
        getReclamationById(id);
        return reclamationRepository.save(reclamation);
    }

    public void deleteReclamation(Integer id) {
        reclamationRepository.delete(getReclamationById(id));
    }
}

