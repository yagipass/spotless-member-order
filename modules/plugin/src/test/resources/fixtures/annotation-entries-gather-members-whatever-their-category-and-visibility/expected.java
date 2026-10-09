package fixtures;

import org.springframework.context.annotation.Bean;

class PersistenceConfig {
    private final DbProperties props;

    PersistenceConfig(DbProperties props) {
        this.props = props;
    }

    @Bean
    public UserRepository userRepository(JdbcTemplate template) {
        return new UserRepository(template);
    }

    @Bean
    DataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    @Bean
    static PropertySourcesPlaceholderConfigurer placeholders() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @org.springframework.context.annotation.Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    static String url(String host) {
        return "jdbc:postgresql://" + host;
    }

    private HikariConfig hikariConfig() {
        return new HikariConfig();
    }
}
