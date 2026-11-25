package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.Moniteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MoniteurRepository extends JpaRepository<Moniteur, Integer> {
    List<Moniteur> findByFormationId(Integer formationId);

    @Query("select m from Moniteur m join fetch m.utilisateur "
            + "join m.formation f where f.id = :formationId order by m.id")
    List<Moniteur> findWithUserByFormationId(@Param("formationId") Integer formationId);

    Optional<Moniteur> findByUtilisateurEmailIgnoreCase(String email);
}
