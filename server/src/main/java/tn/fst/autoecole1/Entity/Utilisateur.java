package tn.fst.autoecole1.Entity;

import tn.fst.autoecole1.Entity.Enum.Role;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity @Table(name = "utilisateurs")
@Getter @Setter @NoArgsConstructor
public class Utilisateur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;          // toujours le hash BCrypt

    @Column(nullable = false, length = 15)
    private String telephone;

    @Column(nullable = false)
    private String adresse = "";

    @Lob
    @Column(name = "photo_profil", columnDefinition = "LONGTEXT")
    private String photoProfil;         // LONGTEXT (base64)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    // Géré par MySQL (ON UPDATE CURRENT_TIMESTAMP) : JPA ne doit pas l'écrire
    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
}
