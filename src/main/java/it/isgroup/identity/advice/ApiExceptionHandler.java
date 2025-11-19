package it.isgroup.identity.advice;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import it.isgroup.identity.domain.Role;
import it.isgroup.identity.exception.EmailAlreadyInUseException;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(EntityNotFoundException.class)
	ResponseEntity<?> notFound(EntityNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<?> badRequest(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(EmailAlreadyInUseException.class)
	ResponseEntity<?> emailAlreadyInUse(EmailAlreadyInUseException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
		var errors = ex.getBindingResult().getFieldErrors().stream()
				.collect(java.util.stream.Collectors.groupingBy(fe -> fe.getField(), java.util.stream.Collectors
						.mapping(fe -> fe.getDefaultMessage(), java.util.stream.Collectors.toList())));
		return ResponseEntity.badRequest().body(Map.of("validationErrors", errors));
	}

	// non è detto che sia il solo caso in cui questa eccezione viene sollevata
	// ma come prima iterazione si suppone che sia così e viene restituito
	// un errore parlante in caso di valore di ROLE non previsto dall'enum
	@ExceptionHandler(HttpMessageNotReadableException.class)
	ResponseEntity<?> badJson(HttpMessageNotReadableException ex) {

		String allowed = Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.joining(", "));
		Map<String, Object> body = Map.of("error", "Valore non valido per il ruolo", "Valori ammessi",
				"[" + allowed + "]");
		return ResponseEntity.badRequest().body(body);
	}
}
