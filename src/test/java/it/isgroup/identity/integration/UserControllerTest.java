package it.isgroup.identity.integration;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.isgroup.identity.domain.Role;
import it.isgroup.identity.rest.dto.UserCreateRequest;
import it.isgroup.identity.rest.dto.UserUpdateRequest;

/**
 * Test di integrazione per il controller utenti.
 * <p>
 * Usa H2 (profilo 'test') e verifica payload HAL/HATEOAS.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.SYSTEM_OUT)
@ActiveProfiles("test")
class UserControllerTest {

	@Autowired
	MockMvc mvc;
	@Autowired
	ObjectMapper om;

	private static final String HAL_JSON = "application/hal+json";
	private static final String API_BASE = "/api/v1/users";

	@Test
	@DisplayName("Creazione utente -> 201 + Location + link self")
	@WithMockUser(username="admin", roles={"ADMIN"})
	void createUser_shouldReturn201_andSelfLink() throws Exception {
		 UserCreateRequest req = new UserCreateRequest("mrossi", "m.rossi@example.com", "RSSMRA80A01H501U", "Mario", "Rossi",
				Set.of(Role.DEVELOPER, Role.REPORTER));

		 MvcResult result = mvc
				.perform(post(API_BASE).contentType(MediaType.APPLICATION_JSON).accept(HAL_JSON)
						.content(om.writeValueAsString(req)))
				.andExpect(status().isCreated()).andExpect(header().string("Location", containsString(API_BASE)))
				.andExpect(content().contentTypeCompatibleWith(HAL_JSON))
				.andExpect(jsonPath("$._links.self.href", containsString(API_BASE)))
				.andExpect(jsonPath("$.email").value("m.rossi@example.com")).andReturn();

		// Optional: follow Location and verify
		String location = result.getResponse().getHeader("Location");
		mvc.perform(get(location).accept(HAL_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$._links.self.href", containsString(location)))
				.andExpect(jsonPath("$.username").value("mrossi"));
	}
	
	@Test
	@DisplayName("Creazione utente non permessa -> 403")
	@WithMockUser(username="reader", roles={"USER"})
	void forbiddedn_createUser_shouldReturn403() throws Exception {
		 UserCreateRequest req = new UserCreateRequest("mrossi", "m.rossi@example.com", "RSSMRA80A01H501U", "Mario", "Rossi",
				Set.of(Role.DEVELOPER, Role.REPORTER));

		mvc.perform(post(API_BASE).contentType(MediaType.APPLICATION_JSON).accept(HAL_JSON)
			.content(om.writeValueAsString(req)))
			.andExpect(status().isForbidden());

	}

	@Test
	@DisplayName("Lista utenti (paged HAL) -> contiene _embedded e link di navigazione")
	@WithMockUser(username="admin", roles={"ADMIN"})
	void listUsers_shouldReturnHalPagedModel() throws Exception {
		// ensure at least one user exists
		UserCreateRequest req = new UserCreateRequest("pgalli", "p.galli@example.com", null, "Paolo", "Galli", Set.of(Role.REPORTER));
		mvc.perform(post(API_BASE).contentType(MediaType.APPLICATION_JSON).accept(HAL_JSON)
				.content(om.writeValueAsString(req))).andExpect(status().isCreated());

		mvc.perform(get(API_BASE+"?size=10&page=0").accept(HAL_JSON)).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(HAL_JSON))
				// rel di default per la collezione: userResponseList
				.andExpect(jsonPath("$._embedded.userResponseList", not(empty())))
				.andExpect(jsonPath("$._links.self.href", containsString(API_BASE)))
				.andExpect(jsonPath("$.page.size", is(10))).andExpect(jsonPath("$.page.number", is(0)));
	}

	@Test
	@DisplayName("Aggiornamento utente -> ruoli sostituiti (PUT semantics)")
	@WithMockUser(username="operator", roles={"OPERATOR"})
	void updateUser_shouldReplaceRoles() throws Exception {
		 UserCreateRequest create = new UserCreateRequest("lbianchi", "l.bianchi@example.com", null, "Luca", "Bianchi",
				Set.of(Role.REPORTER));
		 MvcResult createRes = mvc.perform(post(API_BASE).contentType(MediaType.APPLICATION_JSON).accept(HAL_JSON)
				.content(om.writeValueAsString(create))).andExpect(status().isCreated()).andReturn();

		String location = createRes.getResponse().getHeader("Location");

		 UserUpdateRequest update = new UserUpdateRequest("lbianchi", null, "Luca", "Bianchi",
				Set.of(Role.DEVELOPER, Role.MAINTAINER));

		mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON).accept(HAL_JSON)
				.content(om.writeValueAsString(update))).andExpect(status().isOk())
				.andExpect(jsonPath("$.roles", containsInAnyOrder("DEVELOPER", "MAINTAINER")));
	}

	@Test
	@DisplayName("Eliminazione utente -> 204 e poi 404 al recupero")
	@WithMockUser(username="admin", roles={"ADMIN"})
	void deleteUser_thenNotFound() throws Exception {
		 UserCreateRequest req = new UserCreateRequest("tverde", "t.verde@example.com", null, "Tina", "Verde", Set.of(Role.OPERATOR));
		 MvcResult res = mvc.perform(post(API_BASE).contentType(MediaType.APPLICATION_JSON).accept(HAL_JSON)
				.content(om.writeValueAsString(req))).andExpect(status().isCreated()).andReturn();
		String location = res.getResponse().getHeader("Location");

		mvc.perform(delete(location)).andExpect(status().isNoContent());
		mvc.perform(get(location).accept(HAL_JSON)).andExpect(status().isNotFound());
	}
}