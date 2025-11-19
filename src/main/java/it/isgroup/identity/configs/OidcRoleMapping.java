package it.isgroup.identity.configs;

public class OidcRoleMapping {
	
}

//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
//import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
//import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//
//import it.isgroup.identity.keycloak.KeycloakAuthoritiesConverter;
//
//
//
//@Configuration
//public class OidcRoleMapping {
//
//	@Bean
//	OidcUserService oidcUserService() {
//		OidcUserService delegate = new OidcUserService();
//
//		return new OidcUserService() {
//			@Override
//			public OidcUser loadUser(OidcUserRequest userRequest) {
//				OidcUser user = delegate.loadUser(userRequest);
//
//				// Start with whatever Spring already gave us (usually SCOPE_*).
//				Set<GrantedAuthority> mapped = new HashSet<>(user.getAuthorities());
//
//				// 1) Try to extract Keycloak roles from ID token / userinfo claims
//				Map<String, Object> claims = user.getClaims();
//				mapped.addAll(extractAuthoritiesFromClaims(claims, "demo-task")); // your clientId
//
//				// 2) If none found in ID token, fall back to ACCESS TOKEN (preferred source in
//				// Keycloak)
//				if (mapped.stream().noneMatch(a -> a.getAuthority().startsWith("ROLE_"))) {
//					String issuer = userRequest.getClientRegistration().getProviderDetails().getIssuerUri();
//					String accessToken = userRequest.getAccessToken().getTokenValue();
//
//					JwtDecoder decoder = NimbusJwtDecoder.withIssuerLocation(issuer).build();
//					Jwt jwt = decoder.decode(accessToken); // validated against JWKs
//					mapped.addAll(KeycloakAuthoritiesConverter.fromJwt(jwt));
//				}
//
//				// Build a new DefaultOidcUser with our enriched authorities
//				String nameAttr = Optional.ofNullable(userRequest.getClientRegistration().getProviderDetails()
//						.getUserInfoEndpoint().getUserNameAttributeName()).filter(s -> !s.isBlank())
//						.orElse("preferred_username");
//
//				return new DefaultOidcUser(mapped, user.getIdToken(), user.getUserInfo(), nameAttr);
//			}
//		};
//	}
//
//	@SuppressWarnings("unchecked")
//	private static Collection<GrantedAuthority> extractAuthoritiesFromClaims(Map<String, Object> claims,
//			String clientId) {
//		ArrayList<GrantedAuthority> out = new ArrayList<GrantedAuthority>();
//
//		// realm_access.roles
//		Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
//		if (realmAccess != null) {
//			List<String> roles = (List<String>) realmAccess.getOrDefault("roles", List.of());
//			roles.forEach(r -> out.add(new SimpleGrantedAuthority("ROLE_" + r)));
//		}
//
//		// resource_access.<clientId>.roles
//		Map<String, Object> resourceAccess = (Map<String, Object>) claims.get("resource_access");
//		if (resourceAccess != null) {
//			Map<String, Object> client = (Map<String, Object>) resourceAccess.get(clientId);
//			if (client != null) {
//				List<String> roles = (List<String>) client.getOrDefault("roles", List.of());
//				roles.forEach(r -> out.add(new SimpleGrantedAuthority("ROLE_" + r)));
//			}
//		}
//		return out;
//	}
//}
