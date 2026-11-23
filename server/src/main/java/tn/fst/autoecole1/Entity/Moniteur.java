package tn.fst.autoecole1.Entity;

import tn.fst.autoecole1.Entity.Enum.Voiture;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "moniteurs")
@Getter @Setter @NoArgsConstructor
public class Moniteur {
    @Id
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_formation")
    private Formation formation;

    @Convert(converter = VoitureConverter.class)
    @Column(nullable = false)
    private Voiture voiture;
}