package com.example.employee_management_system.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("!test")
public class FlywayConfig {

    @Autowired
    private DataSource dataSource;
/// by me
/// baselineOnMigrate(true) = useful when integrating Flyway into an already existing production database so Flyway starts tracking migrations from a specific baseline version instead of re-running old scripts.
/// validateOnMigrate(true) = before executing migrate, Flyway checks whether already executed migration files still match the local migration files in checksum, version, and description.
/// validateOnMigrate(true) = helps detect accidental modification, deletion, or corruption of migration files after they were already applied to the database.
/// validateOnMigrate(false) = skips validation checks, which is faster but dangerous because schema drift or modified migrations may go unnoticed.
/// outOfOrder(true) = allows Flyway to execute lower-version migrations that were added later even if higher-version migrations already ran previously.
/// Example: if V1 and V3 already executed, Flyway can still execute newly added V2 when outOfOrder(true) is enabled.
/// outOfOrder(false) = Flyway ignores older missing migrations once newer versions were already applied.
/// outOfOrder(true) = useful in team environments where developers may merge migrations in different orders.
/// cleanDisabled(true) = permanently disables Flyway clean command which normally drops all database objects including tables, views, procedures, and data.
/// cleanDisabled(true) = strongly recommended for production environments to prevent accidental full database wipes.
/// cleanDisabled(false) = allows Flyway clean() operation, commonly used only in local development or testing environments.
@Bean
    public Flyway flyway() {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(false)
                .validateOnMigrate(true)
                .outOfOrder(true)
                .cleanDisabled(true)
                .load();

        // Repair the schema history to fix failed migrations
        flyway.repair();

        // Run the migrations - this will throw exception if migration fails
        flyway.migrate();

        return flyway;
    }
}
