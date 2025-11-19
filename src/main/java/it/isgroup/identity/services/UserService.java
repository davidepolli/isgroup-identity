package it.isgroup.identity.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import it.isgroup.identity.rest.dto.UserCreateRequest;
import it.isgroup.identity.rest.dto.UserResponse;
import it.isgroup.identity.rest.dto.UserUpdateRequest;
import jakarta.persistence.EntityNotFoundException;

/**
 * Servizio applicativo per la gestione degli utenti.
 * <p>
 * Interfaccia che definisce le operazioni CRUD sugli utenti.
 * </p>
 *
 * <h2>Note</h2>
 * <ul>
 * <li><strong>Email univoca</strong>: non possono esistere due utenti con la
 * stessa email.</li>
 * <li><strong>Email immutabile</strong>: l'email impostata in fase di creazione
 * non deve essere modificata.</li>
 * <li><strong>Gestione ruoli</strong>: in aggiornamento (PUT = sostituzione completa).</li>
 * </ul>
 */
public interface UserService {

	/**
	 * Restituisce una pagina di utenti secondo i parametri di
	 * paginazione/ordinamento.
	 *
	 * @param pageable parametri di paginazione e ordinamento (numero pagina,
	 *                 dimensione, sort)
	 * @return pagina di {@link UserResponse}
	 * @throws IllegalArgumentException se {@code pageable} contiene valori non
	 *                                  validi
	 */
	Page<UserResponse> list(Pageable pageable);

	/**
	 * Restituisce il dettaglio di un utente dato il suo identificativo.
	 *
	 * @param id identificativo tecnico dell'utente
	 * @return {@link UserResponse} corrispondente
	 * @throws EntityNotFoundException se l'utente non esiste
	 */
	UserResponse get(Long id);

	/**
	 * Crea un nuovo utente.
	 * <p>
	 * Vincoli:
	 * <ul>
	 * <li>L'email deve essere valida e <strong>univoca</strong>.</li>
	 * <li>L'email è <strong>immutabile</strong> dopo la creazione.</li>
	 * <li>Almeno un ruolo deve essere presente, se richiesto dalle regole di
	 * dominio.</li>
	 * </ul>
	 *
	 * @param req dati necessari alla creazione dell'utente
	 * @return l'utente creato in forma di {@link UserResponse}
	 * @throws IllegalArgumentException se l'email è già in uso o i dati non sono
	 *                                  coerenti
	 */
	UserResponse create(UserCreateRequest req);

	/**
	 * Aggiorna i campi consentiti di un utente esistente (PUT semantico).
	 * <p>
	 * La proprietà <em>email</em> rimane immutabile e non può essere aggiornata. I
	 * ruoli sono gestiti con semantica di <strong>sostituzione</strong> (il set
	 * ricevuto rimpiazza quello esistente).
	 * </p>
	 *
	 * @param id  identificativo dell'utente da aggiornare
	 * @param req dati di aggiornamento (senza email)
	 * @return rappresentazione aggiornata {@link UserResponse}
	 * @throws EntityNotFoundException  se l'utente non esiste
	 * @throws IllegalArgumentException se i dati risultano non validi
	 */
	UserResponse update(Long id, UserUpdateRequest req);

	/**
	 * Elimina definitivamente un utente.
	 *
	 * @param id identificativo dell'utente da eliminare
	 * @throws EntityNotFoundException se l'utente non esiste
	 */
	void delete(Long id);
}