package tn.fst.autoecole1.Entity;



import tn.fst.autoecole1.Entity.Enum.StatutExamen;
import tn.fst.autoecole1.Entity.Enum.TypeExamen;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "examens")
@Getter @Setter @NoArgsConstructor
public class Examen {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_examen", nullable = false)
    private TypeExamen typeExamen;

    @Column(name = "date_examen", nullable = false)
    private LocalDateTime dateExamen;

    @Column(nullable = false)
    private String lieu;

    @Column(nullable = false)
    private int capacite = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutExamen statut = StatutExamen.programme;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;
}
