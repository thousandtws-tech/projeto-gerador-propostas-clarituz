package com.clarituz.application.proposta;

import jakarta.annotation.PostConstruct;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        try {
            String dbName = jdbcTemplate.queryForObject("SELECT CURRENT_DATABASE()", String.class);
            boolean isH2 = dbName != null && dbName.equalsIgnoreCase("PROPOSTAS");
            if (!isH2) {
                log.debug("DatabaseInitializer: banco não-H2 detectado ({}), pulando migração H2.", dbName);
                return;
            }
            jdbcTemplate.execute("ALTER TABLE proposta ALTER COLUMN status VARCHAR(20)");

            List<String> constraints = jdbcTemplate.queryForList(
                "SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.CONSTRAINTS WHERE UPPER(TABLE_NAME) = 'PROPOSTA' AND UPPER(CHECK_EXPRESSION) LIKE '%STATUS%'",
                String.class
            );

            for (String constraintName : constraints) {
                try {
                    jdbcTemplate.execute("ALTER TABLE proposta DROP CONSTRAINT IF EXISTS " + constraintName);
                    log.info("Restrição H2 antiga na coluna status removida: {}", constraintName);
                } catch (Exception ex) {
                    log.warn("Falha ao remover restrição {}", constraintName, ex);
                }
            }
        } catch (Exception e) {
            log.debug("Inicialização de banco ignorada: {}", e.getMessage());
        }
    }
}
