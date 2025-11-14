package it.isgroup.identity.rest.dto;

import java.util.Set;

import it.isgroup.identity.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(@NotBlank @Size(max = 50) String username, @Size(max = 16) String taxCode,
		@Size(max = 100) String firstName, @Size(max = 100) String lastName, @NotNull Set<Role> roles) {
}