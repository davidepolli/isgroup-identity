package it.isgroup.identity.keycloak;


public class KeycloakAuthoritiesConverter {
	
}


//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Stream;
//
//
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.jwt.Jwt;
//
//
//public class KeycloakAuthoritiesConverter {
//	
//	private static final String CLIENT_ID = "demo-task";
//
//	public static Collection<GrantedAuthority> fromJwt(Jwt jwt) {
//		var realmRoles = Optional.ofNullable(jwt.getClaimAsMap("realm_access"))
//				.map(m -> (List<String>) m.getOrDefault("roles", List.of())).orElse(List.of());
//
//		var clientRoles = Optional.ofNullable(jwt.getClaimAsMap("resource_access"))
//				.map(m -> (Map<String, Object>) m.get(CLIENT_ID))
//				.map(m -> (List<String>) ((Map<String, Object>) m).getOrDefault("roles", List.of())).orElse(List.of());
//
//		var scopes = Optional.ofNullable(jwt.getClaimAsString("scope")).map(s -> Arrays.asList(s.split(" ")))
//				.orElse(List.of());
//
//		var strings = Stream
//				.concat(Stream.concat(realmRoles.stream().map(r -> "ROLE_" + r),
//						clientRoles.stream().map(r -> "ROLE_" + r)), scopes.stream().map(s -> "SCOPE_" + s))
//				.distinct().toList();
//
//		return strings.stream().map(SimpleGrantedAuthority::new).map(GrantedAuthority.class::cast).toList();
//	}
//}