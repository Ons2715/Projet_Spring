package tn.fst.autoecole1.Entity;

import tn.fst.autoecole1.Entity.Enum.StatutPaiement;
import tn.fst.autoecole1.Entity.Enum.MethodePaiement;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "paiements")
@Getter @Setter @NoArgsConstructor
public class Paiement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tranche_id")
    private TranchePaiement tranche;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statut = StatutPaiement.en_attente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MethodePaiement methode = MethodePaiement.especes;

    @CreationTimestamp
    @Column(name = "date_paiement", updatable = false)
    private LocalDateTime datePaiement;

    private String reference;
}