package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.Moniteur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MoniteurRepository extends JpaRepository<Moniteur, Integer> {
    List<Moniteur> findByFormationId(Integer formationId);
}
