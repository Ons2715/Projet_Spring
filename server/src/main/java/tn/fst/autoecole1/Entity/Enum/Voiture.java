package tn.fst.autoecole1.Entity.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Voiture {
    KIA_PICANTO("Kia Picanto"),
    RENAULT_CLIO("Renault Clio");

    private final String libelle;

    Voiture(String libelle) { this.libelle = libelle; }

    @JsonValue
    public String getLibelle() { return libelle; }

    @JsonCreator
    public static Voiture fromLibelle(String s) {
        for (Voiture v : values()) if (v.libelle.equalsIgnoreCase(s)) return v;
        throw new IllegalArgumentException("Voiture inconnue : " + s);
    }
}