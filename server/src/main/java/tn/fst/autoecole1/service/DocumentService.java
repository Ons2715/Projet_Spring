
package tn.fst.autoecole1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.fst.autoecole1.Entity.Document;
import tn.fst.autoecole1.repository.DocumentRepository;

import java.util.List;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document getDocumentById(Integer id) {
        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Document introuvable"));
    }

    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }

    public Document updateDocument(Integer id, Document document) {
        // À compléter avec les setters de ton entité.
        return documentRepository.save(document);
    }

    public void deleteDocument(Integer id) {
        Document document = getDocumentById(id);
        documentRepository.delete(document);
    }
}
