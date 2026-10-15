package fixtures;

class PersistenceConfig {
    @Deprecated
    DataSource dataSource(String url, String user) {
        return dataSource(url);
    }

    private HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    DataSource dataSource(String url) {
        return new HikariDataSource(hikariConfig());
    }

    @Bean
    DataSource dataSource() {
        return dataSource("jdbc:postgresql://localhost");
    }

    @Deprecated
    void close() {
    }
}
