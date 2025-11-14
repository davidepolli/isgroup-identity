package it.isgroup.identity.rest;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.isgroup.identity.rest.dto.UserCreateRequest;
import it.isgroup.identity.rest.dto.UserResponse;
import it.isgroup.identity.rest.dto.UserUpdateRequest;
import it.isgroup.identity.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService service;

	@GetMapping
	public Page<UserResponse> list(Pageable pageable) {
		return service.list(pageable);
	}

	@GetMapping("/{id}")
	public UserResponse get(@PathVariable Long id) {
		return service.get(id);
	}

	@PostMapping
	public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest req) {
		UserResponse user = service.create(req);
		return ResponseEntity.created(URI.create("/api/users/" + user.id())).body(user);
	}

	@PutMapping("/{id}")
	public UserResponse update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest req) {
		return service.update(id, req);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
