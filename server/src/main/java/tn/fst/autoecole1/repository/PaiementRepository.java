package tn.fst.autoecole1.repository;



import tn.fst.autoecole1.Entity.Paiement;
import tn.fst.autoecole1.Entity.Enum.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Integer> {
    List<Paiement> findByTrancheIdOrderByDatePaiement(Integer trancheId);

    /** Somme des paiements d'une tranche ayant exactement ce statut. */
    @Query("select coalesce(sum(p.montant), 0) from Paiement p where p.tranche.id = :id and p.statut = :statut")
    BigDecimal sumByTrancheAndStatut(@Param("id") Integer trancheId, @Param("statut") StatutPaiement statut);

    /** Somme des paiements d'une tranche dont le statut est différent (ex. tout sauf rembourse). */
    @Query("select coalesce(sum(p.montant), 0) from Paiement p where p.tranche.id = :id and p.statut <> :statut")
    BigDecimal sumByTrancheAndStatutNot(@Param("id") Integer trancheId, @Param("statut") StatutPaiement statut);
}
