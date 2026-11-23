package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.Formation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormationRepository extends JpaRepository<Formation, Integer> {
    boolean existsByNom(String nom);
    boolean existsByNomAndIdNot(String nom, Integer id);
}
