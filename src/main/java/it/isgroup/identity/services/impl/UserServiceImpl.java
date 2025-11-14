package it.isgroup.identity.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.isgroup.identity.jpa.entities.User;
import it.isgroup.identity.jpa.repository.UserRepository;
import it.isgroup.identity.mapper.UserMapper;
import it.isgroup.identity.rest.dto.UserCreateRequest;
import it.isgroup.identity.rest.dto.UserResponse;
import it.isgroup.identity.rest.dto.UserUpdateRequest;
import it.isgroup.identity.services.UserService;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository repo;
	@Autowired
	private UserMapper mapper;

	@Transactional(readOnly = true)
	public Page<UserResponse> list(Pageable pageable) {
		return repo.findAll(pageable).map(mapper::toResponse);
	}

	@Transactional(readOnly = true)
	public UserResponse get(Long id) {
		var u = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
		return mapper.toResponse(u);
	}

	public UserResponse create(UserCreateRequest req) {
		if (repo.existsByEmail(req.email()))
			throw new IllegalArgumentException("Email already in use");
		User saved = repo.save(mapper.toEntity(req));
		return mapper.toResponse(saved);
	}

	public UserResponse update(Long id, UserUpdateRequest req) {
		var u = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
		// email remains unchanged (not in DTO)
		mapper.updateEntity(u, req);

		// ensure roles replacement semantics (optional; comment out if not needed)
		u.getRoles().clear();
		u.getRoles().addAll(req.roles());

		return mapper.toResponse(u);
	}

	public void delete(Long id) {
		if (!repo.existsById(id))
			throw new EntityNotFoundException("User not found: " + id);
		repo.deleteById(id);
	}
}
