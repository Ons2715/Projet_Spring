package tn.fst.autoecole1.Entity;



import tn.fst.autoecole1.Entity.Enum.StatutDocument;
import tn.fst.autoecole1.Entity.Enum.TypeDocument;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "documents")
@Getter @Setter @NoArgsConstructor
public class Document {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocument type;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "fichier_chemin", nullable = false)
    private String fichierChemin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDocument statut = StatutDocument.en_attente;

    @Column(name = "motif_refus", columnDefinition = "TEXT")
    private String motifRefus;

    @CreationTimestamp
    @Column(name = "date_depot", updatable = false)
    private LocalDateTime dateDepot;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "valide_par")
    private Utilisateur validePar;      // l'admin qui a validé ou refusé
}
