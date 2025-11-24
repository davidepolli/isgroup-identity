package it.isgroup.identity.rest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.SortDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.isgroup.identity.audit.Audit;
import it.isgroup.identity.rest.assembler.UserModelAssembler;
import it.isgroup.identity.rest.dto.UserCreateRequest;
import it.isgroup.identity.rest.dto.UserResponse;
import it.isgroup.identity.rest.dto.UserUpdateRequest;
import it.isgroup.identity.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
//@SecurityRequirement(name = "keycloak-oauth")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService service;
	private final UserModelAssembler assembler;
	private final PagedResourcesAssembler<UserResponse> pagedAssembler;

	public UserController(UserService service, UserModelAssembler assembler,
			PagedResourcesAssembler<UserResponse> pagedAssembler) {
		this.service = service;
		this.assembler = assembler;
		this.pagedAssembler = pagedAssembler;
	}

	 /**
	   * Elenco paginato degli utenti.
	   *
	   * @param pageable parametri di paginazione (page, size, sort)
	   * @return PagedModel HAL con i link di navigazione
	   */
	  @Operation(summary = "Lista utenti (paginata)",
	      description = "Restituisce una collezione HAL di utenti con link di navigazione")
	  @ApiResponses({
	      @ApiResponse(responseCode = "200", description = "OK",
	          content = @Content(mediaType = "application/hal+json",
	              schema = @Schema(implementation = PagedModel.class)))
	  })
	  @GetMapping(produces = { "application/hal+json" })
	  @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','USER')")
	  @Audit(action = "USER_LIST", resource = "User")
	  public PagedModel<EntityModel<UserResponse>> list(
		@ParameterObject
		@Parameter(description = "Parametri di paginazione")
	    @PageableDefault(size = 20)
	    @SortDefault.SortDefaults({
	        @SortDefault(sort = "email", direction = Sort.Direction.ASC)
	    })Pageable pageable) {

	    Page<UserResponse> page = service.list(pageable);
	    return pagedAssembler.toModel(
	        page,
	        assembler,
	        linkTo(methodOn(UserController.class).list(pageable)).withSelfRel()
	    );
	  }

	  /**
	   * Dettaglio utente per id.
	   */
	  @Operation(summary = "Dettaglio utente", description = "Restituisce una risorsa HAL per l'utente richiesto")
	  @ApiResponses({
	      @ApiResponse(responseCode = "200", description = "OK",
	          content = @Content(mediaType = "application/hal+json",
	              schema = @Schema(implementation = EntityModel.class))),
	      @ApiResponse(responseCode = "404", description = "Utente non trovato")
	  })
	  @GetMapping(value = "/{id}", produces = { "application/hal+json" })
	  @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR','USER')")
	  @Audit(action = "USER_READ", resource = "User", idArg = "id")
	  public EntityModel<UserResponse> get(@PathVariable Long id) {
	    return assembler.toModel(service.get(id));
	  }

	  /**
	   * Crea un nuovo utente.
	   */
	  @Operation(summary = "Crea utente", description = "Crea un nuovo utente e restituisce la risorsa HAL creata")
	  @ApiResponses({
	      @ApiResponse(responseCode = "201", description = "Creato"),
	      @ApiResponse(responseCode = "400", description = "Dati non validi"),
	      @ApiResponse(responseCode = "409", description = "Email già in uso")
	  })
	  @PostMapping(consumes = "application/json", produces = { "application/hal+json" })
	  @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
	  @Audit(action = "USER_CREATE", resource = "User")
	  public ResponseEntity<EntityModel<UserResponse>> create(@Valid @RequestBody UserCreateRequest req) {
	    var out = service.create(req);
	    var model = assembler.toModel(out);
	    // Link 'self' come Location
	    URI self = model.getRequiredLink(IanaLinkRelations.SELF).toUri();
	    return ResponseEntity.created(self).body(model);
	  }

	  /**
	   * Aggiorna un utente esistente (email immutabile).
	   */
	  @Operation(summary = "Aggiorna utente", description = "Aggiorna campi consentiti; l'email resta immutabile")
	  @ApiResponses({
	      @ApiResponse(responseCode = "200", description = "OK"),
	      @ApiResponse(responseCode = "404", description = "Utente non trovato")
	  })
	  @PutMapping(value = "/{id}", consumes = "application/json", produces = { "application/hal+json" })
	  @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
	  @Audit(action = "USER_UPDATE", resource = "User", idArg = "id")
	  public EntityModel<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest req) {
	    return assembler.toModel(service.update(id, req));
	  }

	  /**
	   * Elimina un utente per id.
	   */
	  @Operation(summary = "Elimina utente", description = "Rimuove definitivamente l'utente")
	  @ApiResponses({
	      @ApiResponse(responseCode = "204", description = "Eliminato"),
	      @ApiResponse(responseCode = "404", description = "Utente non trovato")
	  })
	  @DeleteMapping("/{id}")
	  @PreAuthorize("hasRole('ADMIN')")
	  @Audit(action = "USER_DELETE", resource = "User", idArg = "id")
	  public ResponseEntity<Void> delete(@PathVariable Long id) {
	    service.delete(id);
	    return ResponseEntity.noContent().build();
	  }
	}