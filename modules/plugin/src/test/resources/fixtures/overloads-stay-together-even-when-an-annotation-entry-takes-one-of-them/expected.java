package fixtures;

class PersistenceConfig {
    @Bean
    DataSource dataSource() {
        return dataSource("jdbc:postgresql://localhost");
    }

    DataSource dataSource(String url) {
        return new HikariDataSource(hikariConfig());
    }

    @Deprecated
    DataSource dataSource(String url, String user) {
        return dataSource(url);
    }

    private HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Deprecated
    void close() {
    }
}
