package tn.fst.autoecole1.Entity;




import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "resultats_quiz")
@Getter @Setter @NoArgsConstructor
public class ResultatQuiz {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eleve_id")
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(nullable = false)
    private int score;                  // 0..100 (CHECK en base)

    @CreationTimestamp
    @Column(name = "date_passage", updatable = false)
    private LocalDateTime datePassage;
}