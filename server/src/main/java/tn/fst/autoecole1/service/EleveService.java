
package tn.fst.autoecole1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.fst.autoecole1.Entity.Eleve;
import tn.fst.autoecole1.repository.EleveRepository;

import java.util.List;

@Service
@Transactional
public class EleveService {

    private final EleveRepository eleveRepository;

    public EleveService(EleveRepository eleveRepository) {
        this.eleveRepository = eleveRepository;
    }

    public List<Eleve> getAllEleves() {
        return eleveRepository.findAll();
    }

    public Eleve getEleveById(Integer id) {
        return eleveRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Élève introuvable"));
    }

    public Eleve saveEleve(Eleve eleve) {
        return eleveRepository.save(eleve);
    }

    public Eleve updateEleve(Integer id, Eleve eleve) {
        getEleveById(id);
        return eleveRepository.save(eleve);
    }

    public void deleteEleve(Integer id) {
        eleveRepository.delete(getEleveById(id));
    }
}

