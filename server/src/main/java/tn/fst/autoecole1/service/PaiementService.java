
package tn.fst.autoecole1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.fst.autoecole1.Entity.Paiement;
import tn.fst.autoecole1.repository.PaiementRepository;

import java.util.List;

@Service
@Transactional
public class PaiementService {

    private final PaiementRepository paiementRepository;

    public PaiementService(PaiementRepository paiementRepository) {
        this.paiementRepository = paiementRepository;
    }

    public List<Paiement> getAllPaiements() {
        return paiementRepository.findAll();
    }

    public Paiement getPaiementById(Integer id) {
        return paiementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Paiement introuvable"));
    }

    public Paiement savePaiement(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    public Paiement updatePaiement(Integer id, Paiement paiement) {
        getPaiementById(id);
        return paiementRepository.save(paiement);
    }

    public void deletePaiement(Integer id) {
        paiementRepository.delete(getPaiementById(id));
    }
}
