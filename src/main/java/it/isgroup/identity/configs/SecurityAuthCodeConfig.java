package it.isgroup.identity.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityAuthCodeConfig {

	@Bean
	SecurityFilterChain security(HttpSecurity http, OidcUserService oidcUserService,
			ClientRegistrationRepository clients) throws Exception {

		OidcClientInitiatedLogoutSuccessHandler oidcLogout = new OidcClientInitiatedLogoutSuccessHandler(clients);
		// {baseUrl} already includes /identity
		oidcLogout.setPostLogoutRedirectUri("{baseUrl}/swagger-ui.html");
		//PathPatternRequestMatcher.withDefaults().matcher("/images/**")
		OrRequestMatcher logoutMatcher = new OrRequestMatcher(new AntPathRequestMatcher("/logout", "GET"),
				new AntPathRequestMatcher("/logout", "POST"));

		http.
		//cors(withDefaults()).
		csrf(csrf -> csrf.disable()) // allow Swagger to POST/PUT without CSRF token
				.authorizeHttpRequests(
						reg -> reg.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
								.requestMatchers("/actuator/health", "/actuator/health/**").permitAll().anyRequest()
								.authenticated())
				.oauth2Login(o -> o
						.userInfoEndpoint(u -> u.oidcUserService(oidcUserService))
						.successHandler((req, res, auth) -> {
							res.sendRedirect(req.getContextPath() + "/swagger-ui.html");
						}))
				.oauth2Client(c -> {
					}).logout(l -> l.logoutRequestMatcher(logoutMatcher).logoutSuccessHandler(oidcLogout)
						.invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID"))
				.exceptionHandling(e -> 
				  e.authenticationEntryPoint(
				    new org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint(
				      "/oauth2/authorization/demo-task"
				    )
				  )
				);

		return http.build();
	}
}