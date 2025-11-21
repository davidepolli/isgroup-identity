// src/main/java/it/isgroup/identity/configs/OidcRoleMapping.java
package it.isgroup.identity.configs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Configuration
public class OidcRoleMapping {

	private static final String CLIENT_ID = "demo-task";

	@Bean
	OidcUserService oidcUserService() {
		 OidcUserService delegate = new OidcUserService();

		return new OidcUserService() {
			@Override
			public OidcUser loadUser(OidcUserRequest req) {
				OidcUser user = delegate.loadUser(req);

				// 1) Start from ID token / userinfo claims
				Map<String, Object> claims = new HashMap<>(user.getClaims());

				// 2) Merge ACCESS TOKEN claims (they contain roles)
				try {
					var parsed = com.nimbusds.jwt.JWTParser.parse(req.getAccessToken().getTokenValue());
					claims.putAll(parsed.getJWTClaimsSet().getClaims());
				} catch (Exception ignored) {
					/* keep going without roles */ }

				// 3) Map roles -> authorities
				Set<GrantedAuthority> authorities = new HashSet<>(user.getAuthorities());
				authorities.addAll(extractAuthoritiesFromClaims(claims));

				String nameAttr = Optional.ofNullable(req.getClientRegistration().getProviderDetails()
						.getUserInfoEndpoint().getUserNameAttributeName()).filter(s -> !s.isBlank())
						.orElse("preferred_username");

				return new DefaultOidcUser(authorities, user.getIdToken(), user.getUserInfo(), nameAttr);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private Collection<? extends GrantedAuthority> extractAuthoritiesFromClaims(Map<String, Object> claims) {
		ArrayList<GrantedAuthority> out = new ArrayList<GrantedAuthority>();

		// realm_access.roles
		Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
		List<String> roles;
		if (realmAccess != null) {
			roles = (List<String>) realmAccess.getOrDefault("roles", List.of());
			roles.forEach(r -> out.add(new SimpleGrantedAuthority("ROLE_" + r)));
		}

		// resource_access.<clientId>.roles
		Map<String, Object> resourceAccess = (Map<String, Object>) claims.get("resource_access");
		if (resourceAccess != null) {
			Map<String, Object> client = (Map<String, Object>) resourceAccess.get(CLIENT_ID);
			if (client != null) {
				roles = (List<String>) client.getOrDefault("roles", List.of());
				roles.forEach(r -> out.add(new SimpleGrantedAuthority("ROLE_" + r)));
			}
		}
		return out;
	}
}