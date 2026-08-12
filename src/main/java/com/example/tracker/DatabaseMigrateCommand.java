package com.example.tracker;

import org.flywaydb.core.Flyway;

public final class DatabaseMigrateCommand {

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "test1234";

    private DatabaseMigrateCommand() {
    }

    public static void main(String[] args) {
        String action = args.length > 0 ? args[0] : "migrate";
        Flyway flyway = Flyway.configure()
                .dataSource(databaseUrl(), databaseUser(), databasePassword())
                .locations("classpath:db/migration")
                .schemas("public")
                .defaultSchema("public")
                .cleanDisabled(false)
                .load();

        if ("repair".equalsIgnoreCase(action)) {
            flyway.repair();
            System.out.println("Flyway repair completed successfully.");
            return;
        }

        if ("reset".equalsIgnoreCase(action)) {
            flyway.clean();
        }

        flyway.migrate();
        System.out.println("Flyway " + action + " completed successfully.");
    }

    private static String databaseUrl() {
        return firstNonBlank(
                System.getenv("APP_DATABASE_URL"),
                System.getenv("SPRING_FLYWAY_URL"),
                System.getenv("SPRING_R2DBC_URL"),
                DEFAULT_URL);
    }

    private static String databaseUser() {
        return firstNonBlank(
                System.getenv("APP_DATABASE_USER"),
                System.getenv("SPRING_FLYWAY_USER"),
                System.getenv("SPRING_R2DBC_USERNAME"),
                DEFAULT_USER);
    }

    private static String databasePassword() {
        return firstNonBlank(
                System.getenv("APP_DATABASE_PASSWORD"),
                System.getenv("SPRING_FLYWAY_PASSWORD"),
                System.getenv("SPRING_R2DBC_PASSWORD"),
                DEFAULT_PASSWORD);
    }

    private static String firstNonBlank(String candidate1, String candidate2, String candidate3, String defaultValue) {
        String[] candidates = { candidate1, candidate2, candidate3 };
        for (String candidate : candidates) {
            if (candidate != null && !candidate.isBlank()) {
                return candidate;
            }
        }
        return defaultValue;
    }
}