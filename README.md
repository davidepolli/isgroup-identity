# Identity (User Management Service)

## Panoramica
Servizio REST per la gestione degli utenti (CRUD) basato su Spring Boot. Comprende:
- Persistenza JPA/Hibernate con migrazioni **Flyway**.

- Profili **H2** (sviluppo) e **PostgreSQL** (Docker).

- Sicurezza con **Keycloak** (OAuth2 Authorization Code per accesso interattivo e/o JWT resource server).

- **Filtraggio per ruolo** degli attributi restituiti (ADMIN/OPERATOR/USER).

- **Audit** applicativo delle azioni

  Documentazione API con **OpenAPI/Swagger** e risorse **HATEOAS**.

## Requisiti
- JDK 21
- Maven 3.9+
- Docker (per avviare un container Postgres) o Postgres standalone, connessione configurabile in application-postgres.yml
- Keycloak con realm e client configurati



Requisiti
