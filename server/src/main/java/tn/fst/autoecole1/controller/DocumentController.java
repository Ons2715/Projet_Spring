package tn.fst.autoecole1.controller;
import tn.fst.autoecole1.Entity.Document;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController extends BaseCrudController<Document> {
    public DocumentController() { super(Document.class); }
}