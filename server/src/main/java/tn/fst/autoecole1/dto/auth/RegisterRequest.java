package tn.fst.autoecole1.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Prénom obligatoire")
    public String firstName;

    @NotBlank(message = "Nom obligatoire")
    public String lastName;

    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email invalide")
    public String email;

    @NotBlank(message = "Téléphone obligatoire")
    public String telephone;

    public String adresse;

    @NotBlank(message = "Mot de passe obligatoire")
    @Size(min = 8, message = "Minimum 8 caractères")
    public String password;

    public Integer formationId;
    public String photo;
}
