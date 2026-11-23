package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.TranchePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TranchePaiementRepository extends JpaRepository<TranchePaiement, Integer> {
    List<TranchePaiement> findByEleveIdOrderByNumeroTranche(Integer eleveId);
    boolean existsByEleveIdAndNumeroTranche(Integer eleveId, int numeroTranche);
}
