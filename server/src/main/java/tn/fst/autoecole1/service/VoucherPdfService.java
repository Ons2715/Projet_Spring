package tn.fst.autoecole1.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import tn.fst.autoecole1.Entity.Reservation;
import tn.fst.autoecole1.Entity.Utilisateur;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class VoucherPdfService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public byte[] generate(Reservation reservation) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(20, 61, 94));
            Font heading = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(20, 61, 94));
            Font body = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.DARK_GRAY);
            Paragraph brand = new Paragraph("EDUCAR", title);
            brand.setAlignment(Element.ALIGN_CENTER);
            document.add(brand);
            Paragraph subtitle = new Paragraph("VOUCHER DE RESERVATION", heading);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            String voucherId = voucherId(reservation.getId());
            addLine(document, "Identifiant unique", voucherId, body, heading);
            addLine(document, "Candidat", studentName(reservation), body, heading);
            addLine(document, "Date", reservation.getDateReservation().format(DATE_FORMAT), body, heading);
            addLine(document, "Heure", reservation.getDateReservation().format(TIME_FORMAT), body, heading);
            addLine(document, "Duree", reservation.getDureeMinutes() + " minutes", body, heading);
            addLine(document, "Lieu de rencontre", safe(reservation.getAdresseDepart()), body, heading);
            addLine(document, "Moniteur", monitorName(reservation), body, heading);
            addLine(document, "Telephone moniteur", monitorPhone(reservation), body, heading);
            addLine(document, "Statut", reservation.getStatut() == null ? "reservee" : reservation.getStatut().name(), body, heading);

            Paragraph note = new Paragraph("Presentez ce voucher au moniteur le jour de votre seance.", body);
            note.setSpacingBefore(12);
            document.add(note);

            String qrValue = "EduCar voucher=" + voucherId + ";reservation=" + reservation.getId();
            BitMatrix matrix = new QRCodeWriter().encode(qrValue, BarcodeFormat.QR_CODE, 220, 220);
            ByteArrayOutputStream qrOutput = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", qrOutput);
            Image qr = Image.getInstance(qrOutput.toByteArray());
            qr.scaleToFit(150, 150);
            qr.setAlignment(Element.ALIGN_CENTER);
            qr.setSpacingBefore(18);
            document.add(qr);

            Paragraph qrLabel = new Paragraph(voucherId, body);
            qrLabel.setAlignment(Element.ALIGN_CENTER);
            document.add(qrLabel);
            document.close();
            return output.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("Impossible de generer le voucher PDF.", exception);
        }
    }

    public static String voucherId(Integer reservationId) {
        return "EDUCAR-" + String.format("%08d", reservationId);
    }

    private void addLine(Document document, String label, String value, Font body, Font heading) throws Exception {
        Paragraph line = new Paragraph();
        line.add(new com.lowagie.text.Chunk(label + " : ", heading));
        line.add(new com.lowagie.text.Chunk(value, body));
        line.setSpacingAfter(9);
        document.add(line);
    }

    private String studentName(Reservation reservation) {
        Utilisateur user = reservation.getEleve() == null ? null : reservation.getEleve().getUtilisateur();
        return user == null ? "Candidat" : safe(user.getNom());
    }

    private String monitorName(Reservation reservation) {
        Utilisateur user = reservation.getMoniteur() == null ? null : reservation.getMoniteur().getUtilisateur();
        return user == null ? "Non attribue" : safe(user.getNom());
    }

    private String monitorPhone(Reservation reservation) {
        Utilisateur user = reservation.getMoniteur() == null ? null : reservation.getMoniteur().getUtilisateur();
        return user == null ? "Non renseigne" : safe(user.getTelephone());
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Non renseigne" : value;
    }
}
