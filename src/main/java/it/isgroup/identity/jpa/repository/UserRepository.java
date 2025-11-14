package it.isgroup.identity.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.isgroup.identity.jpa.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}