package it.isgroup.identity.configs;

public class SecurityAuthCodeConfig {
	
}

//import static org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.withDefaults;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
//import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.OrRequestMatcher;
//
//@Configuration
//@EnableMethodSecurity
//public class SecurityAuthCodeConfig {
//
//	@Bean
//	SecurityFilterChain security(HttpSecurity http, OidcUserService oidcUserService,
//			ClientRegistrationRepository clients) throws Exception {
//
//		OidcClientInitiatedLogoutSuccessHandler oidcLogout = new OidcClientInitiatedLogoutSuccessHandler(clients);
//		oidcLogout.setPostLogoutRedirectUri("{baseUrl}/swagger-ui.html");
//		
//		OrRequestMatcher logoutMatcher = new OrRequestMatcher(
//			        withDefaults().matcher(HttpMethod.GET, "/logout"),
//			        withDefaults().matcher(HttpMethod.POST, "/logout")
//		);
//		
//		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(reg -> reg
//				.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
//				.requestMatchers("/actuator/health", "/actuator/health/**").permitAll().anyRequest().authenticated())
//				.oauth2Login(o -> o.userInfoEndpoint(u -> u.oidcUserService(oidcUserService))) // interactive login against Keycloak
//				.oauth2Client(c -> {
//				});
////				.logout(l -> l
////					      // allow GET /identity/logout (default is POST)
////					      .logoutRequestMatcher(logoutMatcher)
////					      .invalidateHttpSession(true)
////					      .clearAuthentication(true)
////					      .deleteCookies("JSESSIONID")
////					      .logoutSuccessHandler(oidcLogout)   // <-- this triggers Keycloak end-session
////					    );
//
//		return http.build();
//	}
//}