package piron.initializer;

import jakarta.validation.constraints.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class PostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Container
    public static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("user_db")
                    .withUsername("admin")
                    .withPassword("admin")
                    .withReuse(false);

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
        postgreSQLContainer.start();
        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
    }
}