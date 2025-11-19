package it.isgroup.identity.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.isgroup.identity.exception.EmailAlreadyInUseException;
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

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	private final UserRepository repo;
	private final UserMapper mapper;

	public UserServiceImpl(UserRepository repo, UserMapper mapper) {
		this.repo = repo;
		this.mapper = mapper;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserResponse> list(Pageable pageable) {
		if (logger.isDebugEnabled()) {
			logger.debug("[list] Recupero pagina utenti: page [{}], size [{}], sort [{}]",
					pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		}
		return repo.findAll(pageable).map(mapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse get(Long id) {
		if (logger.isDebugEnabled()) {
			logger.debug("[get] Caricamento utente id [{}]", id);
		}
		User user = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
		return mapper.toResponse(user);
	}

	@Override
	public UserResponse create(UserCreateRequest req) {
		if (logger.isDebugEnabled()) {
			logger.debug("[create] Creazione utente email [{}], username [{}]", req.email(),
					req.username());
		}
		if (repo.existsByEmail(req.email())) {
			throw new EmailAlreadyInUseException("Email already in use");
		}
		User saved = repo.save(mapper.toEntity(req));
		return mapper.toResponse(saved);
	}

	/**
	 * è grazie al "JPA dirty-checking" nella transazione che l'entità viene effettivamente persistita
	 */
	@Override
	public UserResponse update(Long id, UserUpdateRequest req) {
		if (logger.isDebugEnabled()) {
			logger.debug("[update] Aggiornamento utente id [{}], username [{}]", id, req.username());
		}
		User user = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
		mapper.updateEntity(user, req);
		// Sostituzione completa dei ruoli
		user.getRoles().clear();
		user.getRoles().addAll(req.roles());
		
		return mapper.toResponse(user);
	}

	@Override
	public void delete(Long id) {
		if (logger.isDebugEnabled()) {
			logger.debug("[delete] Eliminazione utente id [{}]", id);
		}
		if (!repo.existsById(id))
			throw new EntityNotFoundException("User not found: " + id);
		repo.deleteById(id);
	}
}