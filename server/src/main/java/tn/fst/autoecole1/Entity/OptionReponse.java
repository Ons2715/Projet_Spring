package tn.fst.autoecole1.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "options_reponses")
@Getter @Setter @NoArgsConstructor
public class OptionReponse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false)
    private String texte;

    @Column(name = "est_correcte", nullable = false)
    private boolean estCorrecte;        // TINYINT(1) <-> boolean
}