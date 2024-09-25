package singleton;

/// Classe que representa as configurações específicas para um banco de dados PostgreSQL.
public class PostgreSQLConfig extends DatabaseConfig {

    /// Construtor da classe.
    ///
    /// @param jdbcUrl  URL JDBC para conectar ao banco de dados PostgreSQL.
    /// @param username Nome de usuário do banco de dados PostgreSQL.
    /// @param password Senha do banco de dados PostgreSQL.
    public PostgreSQLConfig(String jdbcUrl, String username, String password) {
        super(jdbcUrl, username, password);
    }
}