package tn.fst.autoecole1.Entity;

import tn.fst.autoecole1.Entity.Enum.StatutSeance;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "seances")
@Getter @Setter @NoArgsConstructor
public class Seance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eleve_id")
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "moniteur_id")
    private Moniteur moniteur;

    @Column(name = "date_lecon", nullable = false)
    private LocalDateTime dateLecon;

    @Column(name = "duree_minutes", nullable = false)
    private int dureeMinutes = 60;

    @Column(name = "adresse_depart", columnDefinition = "TEXT")
    private String adresseDepart;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSeance statut = StatutSeance.confirmee;

    @Column(name = "notes_moniteur", columnDefinition = "TEXT")
    private String notesMoniteur;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;
}