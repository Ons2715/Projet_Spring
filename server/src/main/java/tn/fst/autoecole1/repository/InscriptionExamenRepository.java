package tn.fst.autoecole1.repository;


import tn.fst.autoecole1.Entity.InscriptionExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InscriptionExamenRepository extends JpaRepository<InscriptionExamen, Integer> {
    long countByExamenId(Integer examenId);
    boolean existsByEleveIdAndExamenId(Integer eleveId, Integer examenId);
    List<InscriptionExamen> findByEleveId(Integer eleveId);
    List<InscriptionExamen> findByExamenId(Integer examenId);
}
