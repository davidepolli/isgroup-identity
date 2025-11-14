package it.isgroup.identity.rest.dto;

import java.util.Set;

import it.isgroup.identity.domain.Role;

public record UserResponse(Long id, String username, String email, String taxCode, String firstName, String lastName,
		Set<Role> roles) {
}