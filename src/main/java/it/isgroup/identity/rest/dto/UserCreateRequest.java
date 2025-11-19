package it.isgroup.identity.rest.dto;


import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import it.isgroup.identity.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * DTO per la creazione di un nuovo utente.
 * <p>Nota: l'email è univoca e diventa immutabile dopo la creazione.</p>
 */
@Schema(name = "UserCreateRequest", description = "Dati necessari per creare un nuovo utente.")
public record UserCreateRequest(
    @Schema(description = "Username applicativo (univoco a livello di business se richiesto).", example = "mrossi", maxLength = 50)
    @NotBlank @Size(max = 50) String username,

    @Schema(description = "Email univoca dell'utente (immutabile dopo la creazione).", example = "m.rossi@example.com", maxLength = 255)
    @Email @NotBlank @Size(max = 255) String email,

    @Schema(description = "Codice fiscale (opzionale).", example = "RSSMRA80A01H501U", maxLength = 16)
    @Size(max = 16) String taxCode,

    @Schema(description = "Nome proprio.", example = "Mario", maxLength = 100)
    @Size(max = 100) String firstName,

    @Schema(description = "Cognome.", example = "Rossi", maxLength = 100)
    @Size(max = 100) String lastName,

    @Schema(description = "Insieme dei ruoli assegnati all'utente.", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "[\"DEVELOPER\",\"REPORTER\"]")
    @NotEmpty Set<Role> roles
) {}