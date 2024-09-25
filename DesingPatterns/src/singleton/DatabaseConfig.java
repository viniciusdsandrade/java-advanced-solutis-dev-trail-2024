package singleton;

/// Classe abstrata que define as configurações básicas de um banco de dados.
public abstract class DatabaseConfig {

    /// URL JDBC para conectar ao banco de dados.
    private final String jdbcUrl;

    /// Nome de usuário do banco de dados.
    private final String username;

    /// Senha do banco de dados.
    private final String password;

    /// Construtor da classe.
    ///
    /// @param jdbcUrl  URL JDBC para conectar ao banco de dados.
    /// @param username Nome de usuário do banco de dados.
    /// @param password Senha do banco de dados.
    public DatabaseConfig(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    /// Retorna a URL JDBC.
    ///
    /// @return URL JDBC.
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    /// Retorna o nome de usuário.
    ///
    /// @return Nome de usuário.
    public String getUsername() {
        return username;
    }

    /// Retorna a senha.
    ///
    /// @return Senha.
    public String getPassword() {
        return password;
    }
}