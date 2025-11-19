package it.isgroup.identity.configs;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Configurazione OpenAPI/Swagger per la documentazione delle API.
 * <p>
 * Fornisce metadati (titolo, descrizione, versione) e un server predefinito.
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Identity / Users Management API",
        version = "0.1.0",
        description = "API per la gestione utenti e ruoli.",
        contact = @Contact(name = "davidep", email = "davide.polli1@gamil.com")
    ),
    servers = @Server(url = "http://localhost:8080/identity")
)
@SecurityScheme(
    name = "keycloak-oauth",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
    	
        authorizationCode = @OAuthFlow(
            authorizationUrl = "https://idpgw.test4mind.com/realms/demo-interview/protocol/openid-connect/auth",
            tokenUrl         = "https://idpgw.test4mind.com/realms/demo-interview/protocol/openid-connect/token",
            scopes = {
                @OAuthScope(name = "openid",  description = "OpenID Connect"),
                @OAuthScope(name = "profile", description = "Profilo utente"),
                @OAuthScope(name = "email",   description = "Email utente")
            }
        )
    )
)
@Configuration
public class OpenApiConfig {
  // Configurazioni avanzate (security, gruppi) possono essere aggiunte qui.
}