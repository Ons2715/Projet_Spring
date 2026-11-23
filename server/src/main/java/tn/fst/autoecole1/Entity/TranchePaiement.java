package tn.fst.autoecole1.Entity;



import tn.fst.autoecole1.Entity.Enum.StatutTranchePaiement;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "tranches_paiement",
        uniqueConstraints = @UniqueConstraint(name = "uq_eleve_tranche", columnNames = {"eleve_id", "numero_tranche"}))
@Getter @Setter @NoArgsConstructor
public class TranchePaiement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eleve_id")
    private Eleve eleve;

    @Column(name = "numero_tranche", nullable = false)
    private int numeroTranche;

    @Column(name = "montant_prevu", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantPrevu;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTranchePaiement statut = StatutTranchePaiement.en_attente;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;
}