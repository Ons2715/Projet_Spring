
package tn.fst.autoecole1.repository;

import tn.fst.autoecole1.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByEleveId(Integer eleveId);

    List<Reservation> findByEleveIdOrderByDateReservation(Integer eleveId);

    List<Reservation> findByEleveIdOrderByDateReservationDesc(
            Integer eleveId
    );

    List<Reservation> findByMoniteurIdOrderByDateReservation(
            Integer moniteurId
    );

    boolean existsByMoniteurIdAndDateReservation(
            Integer moniteurId,
            LocalDateTime date
    );

    boolean existsByEleveIdAndDateReservation(
            Integer eleveId,
            LocalDateTime date
    );

    @Query("""
            SELECT r.id AS id,
                   r.dateReservation AS dateReservation,
                   r.dureeMinutes AS dureeMinutes,
                   r.statut AS statut
            FROM Reservation r
            """)
    List<BookingSlotView> findAllBookingSlots();

    List<Reservation> findReservationsWithMoniteurByEleveId(Integer userId);

    interface BookingSlotView {
        Integer getId();
        LocalDateTime getDateReservation();
        int getDureeMinutes();
        tn.fst.autoecole1.Entity.Enum.StatutReservation getStatut();
    }
}

