package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.Formation;
import tn.fst.autoecole1.Entity.Enum.Transmission;
import tn.fst.autoecole1.Entity.Enum.TypePratique;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FormationRepository extends JpaRepository<Formation, Integer> {
    boolean existsByNom(String nom);
    Optional<Formation> findByNom(String nom);
    Optional<Formation> findByTypePratiqueAndTransmission(TypePratique typePratique, Transmission transmission);
    boolean existsByNomAndIdNot(String nom, Integer id);
}
