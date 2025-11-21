package it.isgroup.identity.integration;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.isgroup.identity.domain.Role;
import it.isgroup.identity.rest.dto.UserCreateRequest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerValidationTest {

	private static final String API_BASE = "/api/v1/users";
	
	@Autowired
	MockMvc mvc;
	@Autowired
	ObjectMapper om;

	@Test
	@DisplayName("Create -> 400 when mandatory fields missing/invalid")
	@WithMockUser(username="admin", roles={"ADMIN"})
	void create_should400_onValidationErrors() throws Exception {
		var invalid = new UserCreateRequest("", // username blank
				"not-an-email", // invalid email
				"TOO_LONG_CODE_XXXXXXXXXXXXXXXX", // >16
				null, null, Set.of() // empty roles
		);

		mvc.perform(post(API_BASE).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(invalid)))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.validationErrors.username", not(empty())))
				.andExpect(jsonPath("$.validationErrors.email", not(empty())))
				.andExpect(jsonPath("$.validationErrors.taxCode", not(empty())))
				.andExpect(jsonPath("$.validationErrors.roles", not(empty())));
	}

	@Test
	@DisplayName("Create -> 409 on duplicate email")
	@WithMockUser(username="admin", roles={"ADMIN"})
	void create_should409_onDuplicateEmail() throws Exception {
		var ok = new UserCreateRequest("usr1", "dup@example.com", null, null, null, Set.of(Role.OPERATOR));
		mvc.perform(post(API_BASE).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(ok)))
				.andExpect(status().isCreated());

		var dup = new UserCreateRequest("usr2", "dup@example.com", null, null, null, Set.of(Role.OPERATOR));
		mvc.perform(post(API_BASE).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(dup)))
				.andExpect(status().isConflict());
	}
}