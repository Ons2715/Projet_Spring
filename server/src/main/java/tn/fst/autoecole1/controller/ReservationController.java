package tn.fst.autoecole1.controller;

import tn.fst.autoecole1.Entity.Eleve;
import tn.fst.autoecole1.Entity.Enum.StatutReservation;
import tn.fst.autoecole1.Entity.Moniteur;
import tn.fst.autoecole1.Entity.Reservation;
import tn.fst.autoecole1.Entity.Utilisateur;
import tn.fst.autoecole1.repository.EleveRepository;
import tn.fst.autoecole1.repository.MoniteurRepository;
import tn.fst.autoecole1.repository.ReservationRepository;
import tn.fst.autoecole1.service.AuthSessionService;
import tn.fst.autoecole1.service.VoucherPdfService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class ReservationController {
    private static final String ASSIGNED_MONITOR_EMAIL = "nadiamansouri@autoecole.tn";

    private final ReservationRepository reservationRepository;
    private final EleveRepository eleveRepository;
    private final MoniteurRepository moniteurRepository;
    private final AuthSessionService authSessionService;
    private final VoucherPdfService voucherPdfService;

    public ReservationController(
            ReservationRepository reservationRepository,
            EleveRepository eleveRepository,
            MoniteurRepository moniteurRepository,
            AuthSessionService authSessionService,
            VoucherPdfService voucherPdfService) {
        this.reservationRepository = reservationRepository;
        this.eleveRepository = eleveRepository;
        this.moniteurRepository = moniteurRepository;
        this.authSessionService = authSessionService;
        this.voucherPdfService = voucherPdfService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllBookings(
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {
        if (getUserId(authorization) == null) {
            return unauthorized();
        }

        List<Map<String, Object>> bookings = reservationRepository
                .findAllBookingSlots()
                .stream()
                .map(this::toSlotResponse)
                .toList();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getMyBookings(
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {
        Integer userId = getUserId(authorization);
        if (userId == null) {
            return unauthorized();
        }

        return ResponseEntity.ok(
                reservationRepository.findByEleveIdOrderByDateReservation(userId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createBooking(
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @RequestBody CreateBookingRequest request) {
        Integer userId = getUserId(authorization);
        if (userId == null) {
            return unauthorized();
        }

        String validationError = validateRequest(request);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("error", validationError));
        }

        Eleve eleve = eleveRepository.findById(userId).orElse(null);
        if (eleve == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Aucun profil élève associé à ce compte."));
        }
        if (eleve.getFormation() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Choisissez une formation avant de réserver."));
        }

        LocalDateTime start = LocalDateTime.of(request.date, request.time);
        LocalDateTime end = start.plusMinutes(parseDuration(request.duration));
        if (hasOverlap(reservationRepository.findByEleveId(userId), start, end, null)) {
            return conflict("Vous avez déjà une réservation à cet horaire.");
        }

        Moniteur monitor = findAvailableMonitor(eleve, start, end, null);
        if (monitor == null) {
            return conflict("Aucun moniteur disponible pour cette formation à cet horaire.");
        }

        Reservation reservation = new Reservation();
        reservation.setEleve(eleve);
        reservation.setMoniteur(monitor);
        reservation.setDateReservation(start);
        reservation.setDureeMinutes(parseDuration(request.duration));
        reservation.setAdresseDepart(request.place);
        reservation.setNoteClient(request.note);
        reservation.setStatut(StatutReservation.reservee);

        Reservation saved = reservationRepository.saveAndFlush(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getBooking(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {
        Integer userId = getUserId(authorization);
        if (userId == null) {
            return unauthorized();
        }

        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        if (!reservation.getEleve().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous ne pouvez pas consulter cette réservation."));
        }
        return ResponseEntity.ok(toResponse(reservation));
    }

    @GetMapping(value = "/{id}/voucher", produces = MediaType.APPLICATION_PDF_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<?> downloadVoucher(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Integer userId = getUserId(authorization);
        if (userId == null) {
            return unauthorized();
        }
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        if (!reservation.getEleve().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous ne pouvez pas telecharger ce voucher."));
        }

        byte[] pdf = voucherPdfService.generate(reservation);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"voucher-EDUCAR-" + id + ".pdf\"")
                .body(pdf);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateBooking(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @RequestBody CreateBookingRequest request) {
        Integer userId = getUserId(authorization);
        if (userId == null) {
            return unauthorized();
        }

        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        if (!reservation.getEleve().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous ne pouvez pas modifier cette réservation."));
        }

        String validationError = validateRequest(request);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(Map.of("error", validationError));
        }
        Eleve eleve = reservation.getEleve();
        if (eleve.getFormation() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Choisissez une formation avant de réserver."));
        }

        LocalDateTime start = LocalDateTime.of(request.date, request.time);
        LocalDateTime end = start.plusMinutes(parseDuration(request.duration));
        if (hasOverlap(reservationRepository.findByEleveId(userId), start, end, id)) {
            return conflict("Vous avez déjà une réservation à cet horaire.");
        }

        Moniteur monitor = findAvailableMonitor(eleve, start, end, id);
        if (monitor == null) {
            return conflict("Aucun moniteur disponible pour cette formation à cet horaire.");
        }

        reservation.setMoniteur(monitor);
        reservation.setDateReservation(start);
        reservation.setDureeMinutes(parseDuration(request.duration));
        reservation.setAdresseDepart(request.place);
        reservation.setNoteClient(request.note);
        return ResponseEntity.ok(toResponse(reservationRepository.saveAndFlush(reservation)));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteBooking(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {
        Integer userId = getUserId(authorization);
        if (userId == null) {
            return unauthorized();
        }

        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        if (!reservation.getEleve().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Vous ne pouvez pas annuler cette réservation."));
        }

        reservationRepository.delete(reservation);
        return ResponseEntity.noContent().build();
    }

    private String validateRequest(CreateBookingRequest request) {
        if (request == null || request.date == null || request.time == null) {
            return "Date et heure obligatoires.";
        }
        Integer duration = parseDuration(request.duration);
        if (duration == null || duration <= 0 || duration > 480) {
            return "Durée invalide.";
        }
        return null;
    }

    private Integer parseDuration(String duration) {
        if (duration == null || duration.isBlank()) {
            return null;
        }

        String value = duration.trim().toLowerCase(Locale.ROOT);
        try {
            if (value.endsWith("h")) {
                return Integer.parseInt(value.substring(0, value.length() - 1).trim()) * 60;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Moniteur findAvailableMonitor(
            Eleve eleve,
            LocalDateTime requestedStart,
            LocalDateTime requestedEnd,
            Integer ignoredReservationId) {
        Moniteur monitor = moniteurRepository
                .findByUtilisateurEmailIgnoreCase(ASSIGNED_MONITOR_EMAIL)
                .orElse(null);
        if (monitor == null || monitor.getFormation() == null
                || !monitor.getFormation().getId().equals(eleve.getFormation().getId())) {
            return null;
        }

        boolean unavailable = hasOverlap(
                reservationRepository.findByMoniteurIdOrderByDateReservation(monitor.getId()),
                requestedStart,
                requestedEnd,
                ignoredReservationId);
        return unavailable ? null : monitor;
    }

    private boolean hasOverlap(
            List<Reservation> reservations,
            LocalDateTime requestedStart,
            LocalDateTime requestedEnd,
            Integer ignoredReservationId) {
        return reservations.stream()
                .filter(reservation -> ignoredReservationId == null
                        || !ignoredReservationId.equals(reservation.getId()))
                .filter(reservation -> reservation.getStatut() != StatutReservation.annulee)
                .anyMatch(reservation -> {
                    LocalDateTime existingStart = reservation.getDateReservation();
                    if (existingStart == null) {
                        return false;
                    }
                    LocalDateTime existingEnd = existingStart
                            .plusMinutes(reservation.getDureeMinutes());
                    return requestedStart.isBefore(existingEnd)
                            && requestedEnd.isAfter(existingStart);
                });
    }

    private Integer getUserId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7).trim();
        return token.isEmpty() ? null : authSessionService.getUserId(token);
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Session invalide. Veuillez vous reconnecter."));
    }

    private ResponseEntity<Map<String, String>> conflict(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", message));
    }

    private Map<String, Object> toSlotResponse(
            ReservationRepository.BookingSlotView booking) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", booking.getId());
        LocalDateTime dateReservation = booking.getDateReservation();
        response.put("date_lecon", dateReservation);
        response.put("date", dateReservation == null
                ? null
                : dateReservation.toLocalDate().toString());
        response.put("time", dateReservation == null
                ? null
                : dateReservation.toLocalTime().toString());
        response.put("duree_minutes", booking.getDureeMinutes());
        response.put("statut", booking.getStatut() == null
                ? null
                : booking.getStatut().name());
        return response;
    }

    private Map<String, Object> toResponse(Reservation reservation) {
        Map<String, Object> response = new HashMap<>();
        LocalDateTime dateReservation = reservation.getDateReservation();
        response.put("id", reservation.getId());
        response.put("date_lecon", dateReservation);
        response.put("date", dateReservation == null
                ? null
                : dateReservation.toLocalDate().toString());
        response.put("time", dateReservation == null
                ? null
                : dateReservation.toLocalTime().toString());
        response.put("duree_minutes", reservation.getDureeMinutes());
        response.put("statut", reservation.getStatut() == null
                ? null
                : reservation.getStatut().name());
        response.put("adresse_depart", reservation.getAdresseDepart());
        response.put("note", reservation.getNoteClient());

        Moniteur monitor = reservation.getMoniteur();
        if (monitor != null) {
            Utilisateur utilisateur = monitor.getUtilisateur();
            response.put("moniteur_id", monitor.getId());
            response.put("moniteur_nom", utilisateur == null ? null : utilisateur.getNom());
            response.put("moniteur_telephone",
                    utilisateur == null ? null : utilisateur.getTelephone());
        }
        return response;
    }

    public static class CreateBookingRequest {
        public LocalDate date;
        public LocalTime time;
        public String duration;
        public String place;
        public String note;
    }
}
