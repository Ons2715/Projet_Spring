
package tn.fst.autoecole1.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    private final Path rootLocation = Paths.get("uploads");

    public StorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Impossible de créer le dossier uploads", e);
        }
    }

    public String store(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Fichier vide");
            }

            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(
                        originalName.lastIndexOf("."));
            }

            String filename = UUID.randomUUID() + extension;

            Path destination = rootLocation.resolve(filename);

            Files.copy(file.getInputStream(), destination);

            return filename;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors du stockage du fichier", e);
        }
    }

    public String store(byte[] content, String originalName) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Fichier vide");
        }

        String extension = "";
        if (originalName != null) {
            int extensionIndex = originalName.lastIndexOf('.');
            if (extensionIndex >= 0) {
                String candidate = originalName.substring(extensionIndex);
                if (candidate.matches("\\.[A-Za-z0-9]{1,10}")) {
                    extension = candidate;
                }
            }
        }

        String filename = UUID.randomUUID() + extension;
        Path root = rootLocation.toAbsolutePath().normalize();
        Path destination = root.resolve(filename).normalize();
        if (!destination.startsWith(root)) {
            throw new IllegalArgumentException("Chemin de fichier invalide");
        }

        try {
            Files.createDirectories(root);
            Files.write(destination, content);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du stockage du fichier", e);
        }
    }

    public byte[] load(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Nom de fichier manquant");
        }

        Path root = rootLocation.toAbsolutePath().normalize();
        Path file;
        try {
            file = root.resolve(filename).normalize();
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Chemin de fichier invalide", e);
        }
        if (!file.startsWith(root)) {
            throw new IllegalArgumentException("Chemin de fichier invalide");
        }

        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier", e);
        }
    }

    public void delete(String filename) {
        try {
            Path file = rootLocation.resolve(filename).normalize();

            if (!file.getParent().equals(rootLocation.toAbsolutePath().normalize())
                    && !file.startsWith(rootLocation.toAbsolutePath().normalize())) {
                throw new RuntimeException("Chemin de fichier invalide");
            }

            Files.deleteIfExists(file);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors de la suppression du fichier", e);
        }
    }
}
