
package tn.fst.autoecole1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.fst.autoecole1.Entity.Examen;
import tn.fst.autoecole1.repository.ExamenRepository;

import java.util.List;

@Service
@Transactional
public class ExamenService {

    private final ExamenRepository examenRepository;

    public ExamenService(ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    public List<Examen> getAllExamens() {
        return examenRepository.findAll();
    }

    public Examen getExamenById(Integer id) {
        return examenRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Examen introuvable"));
    }

    public Examen saveExamen(Examen examen) {
        return examenRepository.save(examen);
    }

    public Examen updateExamen(Integer id, Examen examen) {
        getExamenById(id);
        return examenRepository.save(examen);
    }

    public void deleteExamen(Integer id) {
        examenRepository.delete(getExamenById(id));
    }
}
