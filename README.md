# Identity (User Management Service)

## Panoramica
Servizio REST per la gestione degli utenti (CRUD) basato su Spring Boot. Comprende:
- Persistenza JPA/Hibernate con migrazioni **Flyway**.

- Profili **H2** (sviluppo) e **PostgreSQL** (Docker).

- Sicurezza con **Keycloak** (OAuth2 Authorization Code per accesso interattivo e/o JWT resource server).

- **Filtraggio per ruolo** degli attributi restituiti (ADMIN/OPERATOR/USER).

- Test minimali per il controller

- **Audit** applicativo delle azioni

  Documentazione API con **OpenAPI/Swagger** e risorse **HATEOAS**.

  

## Requisiti
- JDK 21
- Maven 3.9+
- Docker (per avviare un container Postgres) o Postgres standalone, connessione configurabile in application-postgres.yml
- Keycloak con realm e client configurati



## Utilizzo

Portarsi nella cartella del progetto, ad esempio "D:\intesigroup\projects\isgroup-identity",
assicurarsi di avere nel PATH la versione corretta di java ed eseguire uno dei seguenti comandi

Per Postgres, **di default su docker** (il container si avvia automaticamente, richiede porta 5432 disponibile su host, configurabile da "isgroup-identity\docker\docker-compose.yaml")

- mvnw spring-boot:run -Dspring-boot.run.profiles=postgres

  

Per H2

- mvnw spring-boot:run 

  

Per eseguire test minimali

- mvnw test



Una volta che l'applicazione sarà salita, da browser, raggiungere l'indirizzo
http://localhost:8080/identity/oauth2/authorization/demo-task

Si verrà rediretti all'interfaccia di accesso KC per la login utente, in caso di login corretta sarà utilizzabile
http://localhost:8080/identity/swagger-ui/index.html

per effettuare chiamate autenticate.



## TODO

Non è stato implementato l'evento asincrono, una possibile soluzione sarebbe stata l'utilizzo del pattern
"outbox", inserendo contestualmente alla creazione dell'utente un record in tabella dedicata ("user_event")
e configurando ad esempio un job schedulato per leggere un batch di utenti ed inviare una mail per quelli non ancora notificati.

