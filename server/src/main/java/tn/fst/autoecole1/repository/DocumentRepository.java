package tn.fst.autoecole1.repository;

import tn.fst.autoecole1.Entity.Document;
import tn.fst.autoecole1.Entity.Enum.StatutDocument;
import tn.fst.autoecole1.Entity.Enum.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByUtilisateurId(Integer utilisateurId);
    List<Document> findByUtilisateurIdAndType(Integer utilisateurId, TypeDocument type);
    List<Document> findByStatut(StatutDocument statut);
}
