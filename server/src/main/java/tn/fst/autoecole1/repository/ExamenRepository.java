package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.Examen;
import tn.fst.autoecole1.Entity.Enum.StatutExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamenRepository extends JpaRepository<Examen, Integer> {
    List<Examen> findByStatutOrderByDateExamen(StatutExamen statut);
}