
package tn.fst.autoecole1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.fst.autoecole1.Entity.Moniteur;
import tn.fst.autoecole1.repository.MoniteurRepository;

import java.util.List;

@Service
@Transactional
public class MoniteurService {

    private final MoniteurRepository moniteurRepository;

    public MoniteurService(MoniteurRepository moniteurRepository) {
        this.moniteurRepository = moniteurRepository;
    }

    public List<Moniteur> getAllMoniteurs() {
        return moniteurRepository.findAll();
    }

    public Moniteur getMoniteurById(Integer id) {
        return moniteurRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Moniteur introuvable"));
    }

    public Moniteur saveMoniteur(Moniteur moniteur) {
        return moniteurRepository.save(moniteur);
    }

    public Moniteur updateMoniteur(Integer id, Moniteur moniteur) {
        getMoniteurById(id);
        return moniteurRepository.save(moniteur);
    }

    public void deleteMoniteur(Integer id) {
        moniteurRepository.delete(getMoniteurById(id));
    }
}

