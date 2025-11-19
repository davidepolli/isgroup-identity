package it.isgroup.identity.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ruoli applicativi assegnabili agli utenti.")
public enum Role {
  @Schema(description = "Proprietario: privilegi massimi sull'entità utente.")
  OWNER,
  @Schema(description = "Operatore: operazioni gestionali standard.")
  OPERATOR,
  @Schema(description = "Manutentore: gestione e supporto tecnico.")
  MAINTAINER,
  @Schema(description = "Sviluppatore: profilo tecnico.")
  DEVELOPER,
  @Schema(description = "Reporter: sola consultazione e reportistica.")
  REPORTER
}