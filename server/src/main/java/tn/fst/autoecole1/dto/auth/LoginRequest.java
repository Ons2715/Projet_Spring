package tn.fst.autoecole1.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email invalide")
    public String email;

    @NotBlank(message = "Mot de passe obligatoire")
    public String password;
}
