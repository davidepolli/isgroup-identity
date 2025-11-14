package it.isgroup.identity.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import it.isgroup.identity.rest.dto.UserCreateRequest;
import it.isgroup.identity.rest.dto.UserResponse;
import it.isgroup.identity.rest.dto.UserUpdateRequest;

public interface UserService {
	Page<UserResponse> list(Pageable pageable);

	UserResponse get(Long id);

	UserResponse create(UserCreateRequest req);

	UserResponse update(Long id, UserUpdateRequest req);

	void delete(Long id);
}