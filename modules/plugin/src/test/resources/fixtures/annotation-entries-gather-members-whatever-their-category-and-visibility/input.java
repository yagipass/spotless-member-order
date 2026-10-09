package fixtures;

import org.springframework.context.annotation.Bean;

class PersistenceConfig {
    private HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    public UserRepository userRepository(JdbcTemplate template) {
        return new UserRepository(template);
    }

    static String url(String host) {
        return "jdbc:postgresql://" + host;
    }

    @Bean
    DataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    private final DbProperties props;

    @Bean
    static PropertySourcesPlaceholderConfigurer placeholders() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    PersistenceConfig(DbProperties props) {
        this.props = props;
    }

    @org.springframework.context.annotation.Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
