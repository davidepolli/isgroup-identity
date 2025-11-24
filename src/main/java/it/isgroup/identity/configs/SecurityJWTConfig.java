package it.isgroup.identity.configs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//@EnableMethodSecurity
public class SecurityJWTConfig {

	@Bean @Order(1)
	SecurityFilterChain api(HttpSecurity http, Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter)
			throws Exception {
		http.securityMatcher("/api/**").csrf(csrf -> csrf.disable()).cors(c -> {
		}) // optional: define a CorsConfigurationSource bean
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(
						auth -> auth.requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/**")
								.permitAll().anyRequest().authenticated())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(j -> j.jwtAuthenticationConverter(jwtAuthConverter)))
				.exceptionHandling(e -> e.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) // 401
																												// JSON,
																												// no
																												// redirect
						.accessDeniedHandler(new BearerTokenAccessDeniedHandler()) // 403 JSON
				);
		return http.build();
	}

	@Bean
	Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
		 Converter<Jwt, Collection<GrantedAuthority>> rolesConverter = (Converter<Jwt, Collection<GrantedAuthority>>) jwt -> {
			 ArrayList<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();

			// realm roles → ROLE_*
			Map<String, Object> realm = jwt.getClaim("realm_access");
			if (realm instanceof Map<?, ?> rm) {
				Object rs = rm.get("roles");
				if (rs instanceof Collection<?> roles) {
					roles.stream().map(Object::toString)
							.forEach(r -> auths.add(new SimpleGrantedAuthority("ROLE_" + r)));
				}
			}
			// client roles (demo-task) → ROLE_*
			Map<String, Object> resource = jwt.getClaim("resource_access");
			if (resource instanceof Map<?, ?> ra && ra.get("demo-task") instanceof Map<?, ?> client) {
				Object rs = ((Map<?, ?>) client).get("roles");
				if (rs instanceof Collection<?> roles) {
					roles.stream().map(Object::toString)
							.forEach(r -> auths.add(new SimpleGrantedAuthority("ROLE_" + r)));
				}
			}
			// scopes → SCOPE_*
			String scopeStr = jwt.getClaimAsString("scope");
			if (scopeStr != null) {
				for (var s : scopeStr.split("\\s+")) {
					auths.add(new SimpleGrantedAuthority("SCOPE_" + s));
				}
			}
			return auths;
		};
		JwtAuthenticationConverter delegate = new JwtAuthenticationConverter();
		delegate.setJwtGrantedAuthoritiesConverter(rolesConverter);
		return jwt -> {
			AbstractAuthenticationToken a = delegate.convert(jwt);
			return a;
		};
	}
}
