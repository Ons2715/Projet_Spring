package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    boolean existsByMoniteurIdAndDateReservation(Integer moniteurId, LocalDateTime date);
    boolean existsByEleveIdAndDateReservation(Integer eleveId, LocalDateTime date);
    List<Reservation> findByEleveIdOrderByDateReservation(Integer eleveId);
    List<Reservation> findByMoniteurIdOrderByDateReservation(Integer moniteurId);
}
