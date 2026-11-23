package tn.fst.autoecole1.Entity;


import tn.fst.autoecole1.Entity.Enum.StatutReclamation;



import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "reclamations")
@Getter @Setter @NoArgsConstructor
public class Reclamation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)              // nullable : ON DELETE SET NULL
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)              // nullable : ON DELETE SET NULL
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String raison;

    @Column(name = "piece_nom")
    private String pieceNom;

    @Column(name = "piece_type", length = 120)
    private String pieceType;

    @Column(name = "piece_chemin")
    private String pieceChemin;         // chemin du fichier, plus de base64 en base

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReclamation statut = StatutReclamation.nouvelle;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;
}