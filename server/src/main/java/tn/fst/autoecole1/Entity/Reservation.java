package tn.fst.autoecole1.Entity;




import tn.fst.autoecole1.Entity.Enum.StatutReservation;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "reservations")
@Getter @Setter @NoArgsConstructor
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eleve_id")
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moniteur_id")
    private Moniteur moniteur;

    @Column(name = "date_reservation", nullable = false)
    private LocalDateTime dateReservation;

    @Column(name = "duree_minutes", nullable = false)
    private int dureeMinutes = 60;

    @Column(name = "adresse_depart", columnDefinition = "TEXT")
    private String adresseDepart;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statut = StatutReservation.reservee;

    @Column(name = "note_client", columnDefinition = "TEXT")
    private String noteClient;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seance_id")
    private Seance seance;              // renseignée quand la réservation est confirmée

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;
}