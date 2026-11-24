package tn.fst.autoecole1.Entity;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "eleves")
@Getter @Setter @NoArgsConstructor
public class Eleve {
    @Id
    private Integer id;                 // = utilisateurs.id (clé primaire partagée)

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId                             // l'id de l'élève est copié depuis l'utilisateur
    @JoinColumn(name = "id")
    private Utilisateur utilisateur;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_formation", nullable = true)
    private Formation formation;

    @Column(nullable = false)
    private int progression = 0;

    @Column(name = "acces_code_actif", nullable = false)
    private boolean accesCodeActif = true;

    @Column(name = "heures_solde", nullable = false, precision = 6, scale = 1)
    private BigDecimal heuresSolde = BigDecimal.ZERO;

    @Column(name = "heures_effectuees", nullable = false, precision = 5, scale = 1)
    private BigDecimal heuresEffectuees = BigDecimal.ZERO;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription = LocalDate.now();
}
