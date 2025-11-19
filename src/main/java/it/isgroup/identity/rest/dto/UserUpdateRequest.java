package it.isgroup.identity.rest.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import it.isgroup.identity.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO per l'aggiornamento di un utente esistente.
 * <p>
 * Nota: l'email non è presente perché immutabile.
 * </p>
 */
@Schema(name = "UserUpdateRequest", description = "Dati di aggiornamento per un utente esistente (email esclusa).")
public record UserUpdateRequest(
		@Schema(description = "Username applicativo.", example = "mrossi", maxLength = 50) @NotBlank @Size(max = 50) String username,

		@Schema(description = "Codice fiscale (opzionale).", example = "RSSMRA80A01H501U", maxLength = 16) @Size(max = 16) String taxCode,

		@Schema(description = "Nome proprio.", example = "Mario", maxLength = 100) @Size(max = 100) String firstName,

		@Schema(description = "Cognome.", example = "Rossi", maxLength = 100) @Size(max = 100) String lastName,

		@Schema(description = "Nuovo insieme completo dei ruoli (sostituisce quelli esistenti).", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"DEVELOPER\",\"REPORTER\"]") @NotNull Set<Role> roles) {
}