package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.Utilisateur;
import tn.fst.autoecole1.Entity.Enum.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Utilisateur> findByRole(Role role);
}
