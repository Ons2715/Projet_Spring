package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.Eleve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EleveRepository extends JpaRepository<Eleve, Integer> {
    List<Eleve> findByFormationId(Integer formationId);

    @Query("select e from Eleve e left join fetch e.formation where e.id = :eleveId")
    Optional<Eleve> findByIdWithFormation(@Param("eleveId") Integer eleveId);

    @Query("select e.id as id, u.nom as nom, u.email as email, "
            + "u.telephone as telephone, u.photoProfil as photo, f.nom as formationNom, "
            + "e.heuresEffectuees as heuresEffectuees, f.heuresTotales as heuresTotales, "
            + "e.dateInscription as dateInscription "
            + "from Eleve e join e.utilisateur u join e.formation f "
            + "where exists (select m.id from Moniteur m "
            + "where m.id = :moniteurId and m.formation = f) "
            + "order by u.nom")
    List<MonitorStudentView> findStudentsByMonitorId(@Param("moniteurId") Integer moniteurId);

    interface MonitorStudentView {
        Integer getId();
        String getNom();
        String getEmail();
        String getTelephone();
        String getPhoto();
        String getFormationNom();
        BigDecimal getHeuresEffectuees();
        Integer getHeuresTotales();
        LocalDate getDateInscription();
    }
}
