package it.isgroup.identity.configs;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * configurazione di jackson per effettuare un trim degli input in deserializzazione,
 * prima che intervenga la validazione
 */
@Configuration
public class JacksonConfig {

  @Bean
  Jackson2ObjectMapperBuilderCustomizer trimmingStringsCustomizer() {
    return builder -> {
      SimpleModule module = new SimpleModule();
      module.addDeserializer(String.class, new StdScalarDeserializer<>(String.class) {
        @Override public String deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
          String value = parser.getValueAsString();
          // restituisce null se la stringa è vuota dopo aver rimosso evntuali
          // spazi bianchi all'inizio  e alla fine
          return StringUtils.trimToNull(value);
        }
      });
      builder.modules(module);
      // se fosse richiesto di accettare anche valori lowercase, come "developer"
      //builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    };
  }
}