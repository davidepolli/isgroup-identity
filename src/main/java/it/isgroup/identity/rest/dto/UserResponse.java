package it.isgroup.identity.rest.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import it.isgroup.identity.domain.Role;

/**
 * DTO di risposta per l'utente.
 * <p>
 * Usato nei payload HAL (HATEOAS) come contenuto dell'EntityModel.
 * </p>
 */
@Schema(name = "UserResponse", description = "Rappresentazione di un utente restituita dalle API.")
public record UserResponse(@Schema(description = "Identificativo tecnico dell'utente.", example = "1") Long id,

		@Schema(description = "Username applicativo.", example = "mrossi") String username,

		@Schema(description = "Email univoca (immutabile).", example = "m.rossi@example.com") String email,

		@Schema(description = "Codice fiscale (se presente).", example = "RSSMRA80A01H501U") String taxCode,

		@Schema(description = "Nome proprio.", example = "Mario") String firstName,

		@Schema(description = "Cognome.", example = "Rossi") String lastName,

		@Schema(description = "Insieme dei ruoli assegnati.") Set<Role> roles) {
}