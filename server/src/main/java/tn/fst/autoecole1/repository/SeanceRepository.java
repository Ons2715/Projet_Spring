package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface SeanceRepository extends JpaRepository<Seance, Integer> {
    boolean existsByMoniteurIdAndDateLecon(Integer moniteurId, LocalDateTime date);
    boolean existsByEleveIdAndDateLecon(Integer eleveId, LocalDateTime date);
    List<Seance> findByEleveIdOrderByDateLecon(Integer eleveId);
    List<Seance> findByMoniteurIdOrderByDateLecon(Integer moniteurId);
}
