package tn.fst.autoecole1.repository;


import tn.fst.autoecole1.Entity.Reclamation;
import tn.fst.autoecole1.Entity.Enum.StatutReclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReclamationRepository extends JpaRepository<Reclamation, Integer> {
    List<Reclamation> findByUtilisateurIdOrderByDateCreationDesc(Integer utilisateurId);
    List<Reclamation> findByStatutOrderByDateCreationDesc(StatutReclamation statut);
}
