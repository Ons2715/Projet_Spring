
package tn.fst.autoecole1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.fst.autoecole1.Entity.Seance;
import tn.fst.autoecole1.repository.SeanceRepository;

import java.util.List;

@Service
@Transactional
public class SeanceService {

    private final SeanceRepository seanceRepository;

    public SeanceService(SeanceRepository seanceRepository) {
        this.seanceRepository = seanceRepository;
    }

    public List<Seance> getAllSeances() {
        return seanceRepository.findAll();
    }

    public Seance getSeanceById(Integer id) {
        return seanceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Séance introuvable"));
    }

    public Seance saveSeance(Seance seance) {
        return seanceRepository.save(seance);
    }

    public Seance updateSeance(Integer id, Seance seance) {
        getSeanceById(id);
        return seanceRepository.save(seance);
    }

    public void deleteSeance(Integer id) {
        seanceRepository.delete(getSeanceById(id));
    }
}

