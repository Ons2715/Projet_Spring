package tn.fst.autoecole1.Entity.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Voiture {
    KIA_PICANTO("Kia Picanto"),
    RENAULT_CLIO("Renault Clio"),
    VOLKSWAGEN_POLO("Volkswagen Polo"),
    RENAULT_CLIO_4("Renault Clio 4"),
    PEUGEOT_208("Peugeot 208");

    private final String libelle;

    Voiture(String libelle) { this.libelle = libelle; }

    @JsonValue
    public String getLibelle() { return libelle; }

    @JsonCreator
    public static Voiture fromLibelle(String s) {
        if (s == null) return null;
        String value = s.trim();
        for (Voiture v : values()) {
            if (v.libelle.equalsIgnoreCase(value) || v.name().equalsIgnoreCase(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Voiture inconnue : " + s);
    }
}
