package tn.fst.autoecole1.Entity;



import tn.fst.autoecole1.Entity.Enum.StatutInscriptionExamen;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "inscriptions_examens",
        uniqueConstraints = @UniqueConstraint(name = "uq_eleve_examen", columnNames = {"eleve_id", "examen_id"}))
@Getter @Setter @NoArgsConstructor
public class InscriptionExamen {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eleve_id")
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "examen_id")
    private Examen examen;

    @CreationTimestamp
    @Column(name = "date_inscription", updatable = false)
    private LocalDateTime dateInscription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutInscriptionExamen statut = StatutInscriptionExamen.inscrite;

    @Column(precision = 5, scale = 2)
    private BigDecimal note;

    @Column(columnDefinition = "TEXT")
    private String remarque;
}
