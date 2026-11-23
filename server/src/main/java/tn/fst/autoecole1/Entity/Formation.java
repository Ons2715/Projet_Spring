package tn.fst.autoecole1.Entity;



import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "formations")
@Getter @Setter @NoArgsConstructor
public class Formation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(name = "heures_totales", nullable = false)
    private int heuresTotales = 20;
}