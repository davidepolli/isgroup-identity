package it.isgroup.identity.rest.dto;

import it.isgroup.identity.domain.Role;
import jakarta.validation.constraints.*;
import java.util.Set;

public record UserCreateRequest(@NotBlank @Size(max = 50) String username,
		@Email @NotBlank @Size(max = 255) String email, @Size(max = 16) String taxCode,
		@Size(max = 100) String firstName, @Size(max = 100) String lastName, @NotEmpty Set<Role> roles) {
}
