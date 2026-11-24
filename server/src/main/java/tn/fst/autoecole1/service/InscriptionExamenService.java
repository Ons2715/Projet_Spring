
package tn.fst.autoecole1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.fst.autoecole1.Entity.InscriptionExamen;
import tn.fst.autoecole1.repository.InscriptionExamenRepository;

import java.util.List;

@Service
@Transactional
public class InscriptionExamenService {

    private final InscriptionExamenRepository inscriptionRepository;

    public InscriptionExamenService(
            InscriptionExamenRepository inscriptionRepository) {
        this.inscriptionRepository = inscriptionRepository;
    }

    public List<InscriptionExamen> getAllInscriptions() {
        return inscriptionRepository.findAll();
    }

    public InscriptionExamen getInscriptionById(Integer id) {
        return inscriptionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Inscription introuvable"));
    }

    public InscriptionExamen saveInscription(
            InscriptionExamen inscription) {
        return inscriptionRepository.save(inscription);
    }

    public InscriptionExamen updateInscription(
            Integer id, InscriptionExamen inscription) {
        getInscriptionById(id);
        return inscriptionRepository.save(inscription);
    }

    public void deleteInscription(Integer id) {
        inscriptionRepository.delete(getInscriptionById(id));
    }
}
