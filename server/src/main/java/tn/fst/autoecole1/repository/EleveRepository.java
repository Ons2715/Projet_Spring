package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.Eleve;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EleveRepository extends JpaRepository<Eleve, Integer> {
    List<Eleve> findByFormationId(Integer formationId);
}
