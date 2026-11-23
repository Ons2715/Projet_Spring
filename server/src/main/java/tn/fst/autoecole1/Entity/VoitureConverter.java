package tn.fst.autoecole1.Entity;



import tn.fst.autoecole1.Entity.Enum.Voiture;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class VoitureConverter implements AttributeConverter<Voiture, String> {
    @Override
    public String convertToDatabaseColumn(Voiture v) { return v == null ? null : v.getLibelle(); }

    @Override
    public Voiture convertToEntityAttribute(String s) { return s == null ? null : Voiture.fromLibelle(s); }
}
